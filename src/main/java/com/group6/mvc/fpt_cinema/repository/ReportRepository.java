package com.group6.mvc.fpt_cinema.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.enums.BookingChannel;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;
import com.group6.mvc.fpt_cinema.enums.MembershipLevel;
import com.group6.mvc.fpt_cinema.enums.MovieStatus;
import com.group6.mvc.fpt_cinema.enums.PaymentMethod;
import com.group6.mvc.fpt_cinema.enums.PaymentStatus;
import com.group6.mvc.fpt_cinema.enums.UserStatus;
import com.group6.mvc.fpt_cinema.repository.projection.BookingChannelProjection;
import com.group6.mvc.fpt_cinema.repository.projection.BookingTrendProjection;
import com.group6.mvc.fpt_cinema.repository.projection.MovieCapacityProjection;
import com.group6.mvc.fpt_cinema.repository.projection.MoviePerformanceProjection;
import com.group6.mvc.fpt_cinema.repository.projection.MovieRevenueProjection;
import com.group6.mvc.fpt_cinema.repository.projection.PaymentMethodProjection;
import com.group6.mvc.fpt_cinema.repository.projection.PaymentStatusCountProjection;
import com.group6.mvc.fpt_cinema.repository.projection.PromotionSummaryProjection;
import com.group6.mvc.fpt_cinema.repository.projection.PromotionUsageProjection;
import com.group6.mvc.fpt_cinema.repository.projection.RevenueTrendProjection;
import com.group6.mvc.fpt_cinema.repository.projection.TopCustomerProjection;

/**
 * Single repository for the report management dashboard. Each query aggregates
 * in the database and returns an interface projection (column aliases match the
 * projection getters); ReportMapper turns each row into a response DTO. Date
 * grouping uses MySQL DATE_FORMAT and groups/orders by the "period" alias. Enum
 * constants are unqualified (Hibernate infers the type from the field).
 */
@Repository
public interface ReportRepository extends JpaRepository<Booking, Integer> {

        @Query("""
                        select function('date_format', b.createdAt, '%Y-%m-%d') as period,
                               count(b.id) as bookingCount,
                               sum(case when b.status = COMPLETED then 1 else 0 end) as completedCount,
                               sum(case when b.status = CANCELLED then 1 else 0 end) as cancelledCount
                        from Booking b
                        where b.createdAt >= :start and b.createdAt < :end
                          and (:movieId is null or b.showtime.movie.id = :movieId)
                          and (:statuses is null or b.status in :statuses)
                          and (:channels is null or b.channel in :channels)
                        group by period
                        order by period
                        """)
        List<BookingTrendProjection> findBookingTrend(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("movieId") Integer movieId,
                        @Param("statuses") List<BookingStatus> statuses,
                        @Param("channels") List<BookingChannel> channels);

        @Query("""
                        select b.channel as channel, count(b.id) as bookingCount
                        from Booking b
                        where b.createdAt >= :start and b.createdAt < :end
                          and (:movieId is null or b.showtime.movie.id = :movieId)
                          and (:statuses is null or b.status in :statuses)
                          and (:channels is null or b.channel in :channels)
                        group by b.channel
                        order by count(b.id) desc
                        """)
        List<BookingChannelProjection> findBookingChannelDistribution(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("movieId") Integer movieId,
                        @Param("statuses") List<BookingStatus> statuses,
                        @Param("channels") List<BookingChannel> channels);

        @Query("""
                        select pr.promotionCode as promotionCode, sum(b.discountAmount) as discountAmount
                        from Booking b
                        join b.promotion pr
                        where b.createdAt >= :start and b.createdAt < :end
                          and (:code is null or pr.promotionCode = :code)
                          and (:type is null or pr.promotionType = :type)
                        group by pr.id, pr.promotionCode
                        order by sum(b.discountAmount) desc
                        """)
        List<PromotionUsageProjection> findPromotionUsage(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("code") String code,
                        @Param("type") String type);

        @Query("""
                        select count(distinct pr.id) as totalPromotions,
                               count(b.id) as totalUsage,
                               sum(b.discountAmount) as totalDiscountAmount
                        from Booking b
                        join b.promotion pr
                        where b.createdAt >= :start and b.createdAt < :end
                          and (:code is null or pr.promotionCode = :code)
                          and (:type is null or pr.promotionType = :type)
                        """)
        PromotionSummaryProjection findPromotionSummary(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("code") String code,
                        @Param("type") String type);

        @Query("""
                        select function('date_format', coalesce(p.paidAt, p.createdAt), '%Y-%m-%d') as period,
                               sum(p.amount) as revenue,
                               count(p.id) as orderCount
                        from Payment p
                        join p.booking b
                        where p.status = COMPLETED
                          and coalesce(p.paidAt, p.createdAt) >= :start
                          and coalesce(p.paidAt, p.createdAt) < :end
                          and (:method is null or p.method = :method)
                          and (:completedOnly = false or b.status = COMPLETED)
                        group by period
                        order by period
                        """)
        List<RevenueTrendProjection> findRevenueTrend(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("method") PaymentMethod method,
                        @Param("completedOnly") boolean completedOnly);

        @Query("""
                        select m.title as movieTitle, sum(p.amount) as revenue
                        from Payment p
                        join p.booking b
                        join b.showtime s
                        join s.movie m
                        where p.status = COMPLETED
                          and coalesce(p.paidAt, p.createdAt) >= :start
                          and coalesce(p.paidAt, p.createdAt) < :end
                          and (:method is null or p.method = :method)
                          and (:completedOnly = false or b.status = COMPLETED)
                        group by m.id, m.title
                        order by sum(p.amount) desc
                        """)
        List<MovieRevenueProjection> findMovieRevenue(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("method") PaymentMethod method,
                        @Param("completedOnly") boolean completedOnly);

        @Query("""
                        select count(p.id) as total,
                               sum(case when p.status = COMPLETED then 1 else 0 end) as successful,
                               sum(case when p.status = FAILED then 1 else 0 end) as failed
                        from Payment p
                        where p.createdAt >= :start and p.createdAt < :end
                          and (:status is null or p.status = :status)
                          and (:method is null or p.method = :method)
                        """)
        PaymentStatusCountProjection findPaymentStatusCounts(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("status") PaymentStatus status,
                        @Param("method") PaymentMethod method);

        @Query("""
                        select p.method as paymentMethod, count(p.id) as transactionCount, sum(p.amount) as amount
                        from Payment p
                        where p.createdAt >= :start and p.createdAt < :end
                          and p.status = COMPLETED
                          and (:method is null or p.method = :method)
                        group by p.method
                        order by sum(p.amount) desc
                        """)
        List<PaymentMethodProjection> findPaymentMethodBreakdown(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("method") PaymentMethod method);

        @Query("""
                        select m.id as movieId, m.title as movieTitle,
                               count(t.id) as ticketsSold,
                               count(distinct t.booking.id) as bookingCount,
                               sum(t.price) as revenue
                        from Ticket t
                        join t.showtime s
                        join s.movie m
                        where s.startTime >= :start and s.startTime < :end
                          and (:status is null or m.status = :status)
                        group by m.id, m.title
                        order by count(t.id) desc
                        """)
        List<MoviePerformanceProjection> findMoviePerformance(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("status") MovieStatus status);

        @Query("""
                        select m.id as movieId, count(seat.id) as capacity
                        from Showtime s
                        join s.movie m
                        join Seat seat on seat.room.id = s.room.id
                        where s.startTime >= :start and s.startTime < :end
                          and (:status is null or m.status = :status)
                        group by m.id
                        """)
        List<MovieCapacityProjection> findMovieCapacity(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("status") MovieStatus status);

        @Query("""
                        select count(u.id)
                        from User u
                        where u.role.id = :customerRoleId
                          and (:level is null or u.membershipLevel = :level)
                          and (:status is null or u.status = :status)
                        """)
        Integer countCustomers(
                        @Param("customerRoleId") Integer customerRoleId,
                        @Param("level") MembershipLevel level,
                        @Param("status") UserStatus status);

        @Query("""
                        select count(u.id)
                        from User u
                        where u.role.id = :customerRoleId
                          and u.createdAt >= :start and u.createdAt < :end
                          and (:level is null or u.membershipLevel = :level)
                          and (:status is null or u.status = :status)
                        """)
        Integer countNewCustomers(
                        @Param("customerRoleId") Integer customerRoleId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("level") MembershipLevel level,
                        @Param("status") UserStatus status);

        @Query("""
                        select b.customer.id
                        from Booking b
                        where b.createdAt >= :start and b.createdAt < :end
                          and (:level is null or b.customer.membershipLevel = :level)
                          and (:status is null or b.customer.status = :status)
                        group by b.customer.id
                        having count(b.id) >= 2
                        """)
        List<Integer> findReturningCustomerIds(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("level") MembershipLevel level,
                        @Param("status") UserStatus status);

        @Query("""
                        select b.customer.fullName as customerName,
                               count(b.id) as bookingCount,
                               sum(b.finalAmount) as spending
                        from Booking b
                        where b.createdAt >= :start and b.createdAt < :end
                          and b.status = COMPLETED
                          and (:level is null or b.customer.membershipLevel = :level)
                          and (:status is null or b.customer.status = :status)
                        group by b.customer.id, b.customer.fullName
                        order by sum(b.finalAmount) desc
                        """)
        List<TopCustomerProjection> findTopCustomers(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("level") MembershipLevel level,
                        @Param("status") UserStatus status);
}
