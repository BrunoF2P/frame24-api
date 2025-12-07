-- ============================================================================
-- Create Showtime Availability View
-- ============================================================================
-- Migration: V31__create_showtime_availability_view.sql
-- Description: Creates a view to aggregate showtime details, movie info, and seat availability
-- ============================================================================
DROP VIEW IF EXISTS operations.v_showtime_availability ;
CREATE VIEW operations.v_showtime_availability
AS
  SELECT ss.id AS showtime_id
         , ss.cinema_complex_id
         , cc.company_id
         , ss.room_id
         , r.name AS room_name
         , ss.movie_id
         , m.original_title AS movie_title
         , ss.start_time
         , ss.end_time
         , CAST(r.capacity AS BIGINT) AS total_seats
         , CAST(COALESCE(ss.sold_seats
                         , 0) AS BIGINT) AS seats_sold
         , CAST(COALESCE(ss.blocked_seats
                         , 0) AS BIGINT) AS seats_reserved
         , CAST(GREATEST(0
                         , r.capacity - COALESCE(ss.sold_seats
                                                 , 0) - COALESCE(ss.blocked_seats
                                                                              , 0)) AS BIGINT) AS seats_available
         , CASE
                WHEN r.capacity > 0 THEN CAST((COALESCE(ss.sold_seats
                                                         , 0) * 100.0 / r.capacity) AS DECIMAL (5                                                                                            , 2))
                ELSE 0.00
            END AS occupancy_percentage
         , CASE
                WHEN ss.start_time > CURRENT_TIMESTAMP THEN 'UPCOMING'
                WHEN ss.start_time <= CURRENT_TIMESTAMP
                     AND ss.end_time >= CURRENT_TIMESTAMP THEN 'NOW_SHOWING'
                ELSE 'ENDED'
            END AS session_status
         , ss.base_ticket_price
         , ss.created_at
    FROM operations.showtime_schedule ss
         JOIN operations.cinema_complexes cc
           ON ss.cinema_complex_id = cc.id
         JOIN operations.rooms r
           ON ss.room_id = r.id
         JOIN catalog.movies m
           ON ss.movie_id = m.id ;
 -- Grant permissions if necessary (usually owner has full access)
 -- GRANT SELECT ON operations.v_showtime_availability TO "frame24";
