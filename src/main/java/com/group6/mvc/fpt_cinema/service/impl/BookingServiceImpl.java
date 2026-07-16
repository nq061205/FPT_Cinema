package com.group6.mvc.fpt_cinema.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.dto.request.CreateBookingProductItemRequest;
import com.group6.mvc.fpt_cinema.dto.request.CreateBookingRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewBookingHistoryRequest;
import com.group6.mvc.fpt_cinema.dto.response.CreateBookingResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewBookingHistoryResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.BookingProduct;
import com.group6.mvc.fpt_cinema.entity.Product;
import com.group6.mvc.fpt_cinema.entity.Promotion;
import com.group6.mvc.fpt_cinema.entity.Seat;
import com.group6.mvc.fpt_cinema.entity.Showtime;
import com.group6.mvc.fpt_cinema.entity.Ticket;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.entity.User_Promotion;
import com.group6.mvc.fpt_cinema.enums.BookingChannel;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.ShowtimeStatus;
import com.group6.mvc.fpt_cinema.enums.UserPromotionStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.BookingMapper;
import com.group6.mvc.fpt_cinema.repository.BookingProductRepository;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.repository.ProductRepository;
import com.group6.mvc.fpt_cinema.repository.SeatRepository;
import com.group6.mvc.fpt_cinema.repository.ShowtimeRepository;
import com.group6.mvc.fpt_cinema.repository.TicketRepository;
import com.group6.mvc.fpt_cinema.repository.UserPromotionRepository;
import com.group6.mvc.fpt_cinema.repository.UserRepository;
import com.group6.mvc.fpt_cinema.service.BookingService;

@Service
public class BookingServiceImpl
        extends AbstractCrudService<Booking, Integer>
        implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final ProductRepository productRepository;
    private final BookingProductRepository bookingProductRepository;
    private final UserPromotionRepository userPromotionRepository;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            BookingMapper bookingMapper,
            UserRepository userRepository,
            ShowtimeRepository showtimeRepository,
            SeatRepository seatRepository,
            TicketRepository ticketRepository,
            ProductRepository productRepository,
            BookingProductRepository bookingProductRepository,
            UserPromotionRepository userPromotionRepository) {
        super(bookingRepository);
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userRepository = userRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.productRepository = productRepository;
        this.bookingProductRepository = bookingProductRepository;
        this.userPromotionRepository = userPromotionRepository;
    }

    @Override
    public List<ViewBookingHistoryResponse> getBookingHistory(
            Integer customerId,
            ViewBookingHistoryRequest request) {
        int page = request == null || request.getPage() == null ? 0 : Math.max(request.getPage(), 0);
        int size = request == null || request.getSize() == null ? 5 : Math.max(request.getSize(), 1);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Booking> bookingPage = bookingRepository.findByCustomerId(customerId, pageable);

        return bookingPage.getContent()
                .stream()
                .map(bookingMapper::toBookingHistoryResponse)
                .toList();
    }

    @Override
    @Transactional
    public CreateBookingResponse createBooking(Integer customerId, CreateBookingRequest request) {
        List<Integer> seatIds = request.getSeatIds();
        if (seatIds == null || seatIds.isEmpty()) {
            throw new AppException(ErrorCode.NO_SEATS_SELECTED);
        }

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));
        // Showtime.status is persisted as an enum, not a String. Comparing the
        // enum to the literal "OPEN" made every booking fail, even when the
        // database row was OPEN. Also reject a showtime after it has started;
        // the scheduler may only mark it FINISHED after the movie ends.
        if (showtime.getStatus() != ShowtimeStatus.OPEN
                || !showtime.getStartTime().isAfter(LocalDateTime.now())) {
            throw new AppException(ErrorCode.SHOWTIME_NOT_BOOKABLE);
        }

        Integer roomId = showtime.getRoom().getId();
        List<Seat> seats = seatRepository.findAllById(seatIds);
        for (Seat seat : seats) {
            if (!seat.getRoom().getId().equals(roomId)) {
                throw new AppException(ErrorCode.SEAT_NOT_IN_SHOWTIME_ROOM);
            }
            if (ticketRepository.existsBySeatIdAndShowtimeIdAndStatus(seat.getId(), showtime.getId(), "BOOKED")) {
                throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
            }
        }

        List<BookingProduct> bookingProducts = new ArrayList<>();
        BigDecimal productSubtotal = BigDecimal.ZERO;
        List<CreateBookingProductItemRequest> productItems = request.getProducts();
        if (productItems != null && !productItems.isEmpty()) {
            for (CreateBookingProductItemRequest item : productItems) {
                if (item.getQuantity() == null || item.getQuantity() < 1) {
                    throw new AppException(ErrorCode.INVALID_QUANTITY);
                }
                Product product = productRepository.findByIdAndIsActiveTrue(item.getProductId())
                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                BigDecimal totalPrice = product.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                BookingProduct bp = new BookingProduct();
                bp.setProduct(product);
                bp.setQuantity(item.getQuantity());
                bp.setUnitPrice(product.getPrice());
                bp.setTotalPrice(totalPrice);
                bookingProducts.add(bp);
                productSubtotal = productSubtotal.add(totalPrice);
            }
        }

        BigDecimal ticketSubtotal = showtime.getBasePrice()
                .multiply(BigDecimal.valueOf(seats.size()));
        BigDecimal subtotal = ticketSubtotal.add(productSubtotal);
        BigDecimal discountAmount = BigDecimal.ZERO;
        Promotion promotion = null;

        if (request.getPromotionId() != null) {
            User_Promotion userPromotion = userPromotionRepository
                    .findByUserIdAndPromotionId(customerId, request.getPromotionId())
                    .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));

            if (UserPromotionStatus.USED == userPromotion.getStatus()) {
                throw new AppException(ErrorCode.PROMOTION_ALREADY_USED);
            }

            promotion = userPromotion.getPromotion();
            if (!promotion.getIsActive()) {
                throw new AppException(ErrorCode.PROMOTION_INACTIVE);
            }
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(promotion.getStartDate())) {
                throw new AppException(ErrorCode.PROMOTION_NOT_STARTED);
            }
            if (now.isAfter(promotion.getEndDate())) {
                throw new AppException(ErrorCode.PROMOTION_EXPIRED);
            }

            discountAmount = calculateDiscount(promotion, subtotal);
            userPromotion.setStatus(UserPromotionStatus.USED);
            userPromotion.setUsedAt(now);
            userPromotionRepository.save(userPromotion);
        }

        BigDecimal finalAmount = subtotal.subtract(discountAmount).max(BigDecimal.ZERO);

        String bookingCode = generateBookingCode();
        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setCustomer(customer);
        booking.setShowtime(showtime);
        booking.setPromotion(promotion);
        booking.setChannel(BookingChannel.ONLINE);
        booking.setStatus(BookingStatus.PENDING);
        booking.setSubtotal(subtotal);
        booking.setDiscountAmount(discountAmount);
        booking.setFinalAmount(finalAmount);
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(2));
        Booking savedBooking = bookingRepository.save(booking);

        List<Ticket> tickets = new ArrayList<>();
        for (Seat seat : seats) {
            Ticket ticket = new Ticket();
            ticket.setBooking(savedBooking);
            ticket.setShowtime(showtime);
            ticket.setSeat(seat);
            ticket.setTicketCode(bookingCode + "-" + seat.getSeatRow() + seat.getSeatNumber());
            ticket.setPrice(showtime.getBasePrice());
            ticket.setStatus("RESERVED");
            tickets.add(ticket);
        }
        ticketRepository.saveAll(tickets);

        for (BookingProduct bp : bookingProducts) {
            bp.setBooking(savedBooking);
        }
        bookingProductRepository.saveAll(bookingProducts);

        return bookingMapper.toCreateBookingResponse(savedBooking, tickets);
    }

    @Override
    @Transactional
    public void expireStaleBookings() {
        List<Booking> staleBookings = bookingRepository.findByStatusAndExpiresAtBefore(
                BookingStatus.PENDING, LocalDateTime.now());

        for (Booking booking : staleBookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
            tickets.forEach(t -> t.setStatus("CANCELLED"));
            ticketRepository.saveAll(tickets);

            if (booking.getPromotion() != null) {
                userPromotionRepository
                        .findByUserIdAndPromotionId(booking.getCustomer().getId(), booking.getPromotion().getId())
                        .ifPresent(up -> {
                            up.setStatus(UserPromotionStatus.AVAILABLE);
                            up.setUsedAt(null);
                            userPromotionRepository.save(up);
                        });
            }
        }
    }

    private BigDecimal calculateDiscount(Promotion promotion, BigDecimal subtotal) {
        if ("PERCENTAGE".equals(promotion.getPromotionType())) {
            return subtotal.multiply(promotion.getDiscountValue())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        return promotion.getDiscountValue().min(subtotal);
    }

    private String generateBookingCode() {
        return "BK" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}
