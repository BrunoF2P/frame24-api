package com.frame24.api.operations.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateShowtimeRequest;
import com.frame24.api.operations.application.dto.ShowtimeResponse;
import com.frame24.api.operations.application.dto.UpdateShowtimeRequest;
import com.frame24.api.operations.domain.*;
import com.frame24.api.operations.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeScheduleRepository showtimeRepository;
    private final RoomRepository roomRepository;
    private final AudioTypeRepository audioTypeRepository;
    private final ProjectionTypeRepository projectionTypeRepository;
    private final SessionLanguageRepository sessionLanguageRepository;
    private final SessionStatusRepository sessionStatusRepository;

    private final VShowtimeAvailabilityRepository vShowtimeAvailabilityRepository;
    private final SessionSeatStatusRepository sessionSeatStatusRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public ShowtimeResponse create(CreateShowtimeRequest request, UserPrincipal principal) {
        log.info("Criando sessão para sala: {}, filme: {}, inicio: {}", request.roomId(), request.movieId(),
                request.startTime());

        validateTimes(request.startTime(), request.endTime());

        Room room = roomRepository.findById(request.roomId())
                .filter(r -> r.getCinemaComplex().getCompanyId().equals(principal.getCompanyId()))
                .orElseThrow(() -> new NotFoundException("Sala não encontrada"));

        validateOverlap(room.getId(), request.startTime(), request.endTime(), null);

        AudioType audio = findAudioType(request.audioTypeId(), principal.getCompanyId());
        ProjectionType projection = findProjectionType(request.projectionTypeId(), principal.getCompanyId());
        SessionLanguage language = findSessionLanguage(request.sessionLanguageId(), principal.getCompanyId());
        SessionStatus status = findSessionStatus(request.statusId(), principal.getCompanyId());

        ShowtimeSchedule showtime = new ShowtimeSchedule();
        showtime.setCinemaComplex(room.getCinemaComplex());
        showtime.setRoom(room);
        showtime.setMovieId(request.movieId()); // TODO: Validate Movie existence separately or assume trusting ID for
        // now
        showtime.setStartTime(request.startTime());
        showtime.setEndTime(request.endTime());
        showtime.setAudioType(audio);
        showtime.setProjectionType(projection);
        showtime.setSessionLanguage(language);
        showtime.setStatus(status);
        showtime.setBaseTicketPrice(request.baseTicketPrice());

        // Initializing seat counters
        showtime.setAvailableSeats(room.getCapacity());
        showtime.setSoldSeats(0);
        showtime.setBlockedSeats(0);
        showtime.setCreatedAt(Instant.now());

        showtimeRepository.save(showtime);
        return toResponse(showtime);
    }

    @Transactional(readOnly = true)
    public ShowtimeResponse getById(Long id, UserPrincipal principal) {
        ShowtimeSchedule showtime = findByIdAndCompany(id, principal.getCompanyId());
        return toResponse(showtime);
    }

    @Transactional
    public ShowtimeResponse update(Long id, UpdateShowtimeRequest request, UserPrincipal principal) {
        ShowtimeSchedule showtime = findByIdAndCompany(id, principal.getCompanyId());

        if (request.startTime() != null || request.endTime() != null) {
            Instant newStart = request.startTime() != null ? request.startTime() : showtime.getStartTime();
            Instant newEnd = request.endTime() != null ? request.endTime() : showtime.getEndTime();

            validateTimes(newStart, newEnd);
            validateOverlap(showtime.getRoom().getId(), newStart, newEnd, id);

            showtime.setStartTime(newStart);
            showtime.setEndTime(newEnd);
        }

        if (request.audioTypeId() != null)
            showtime.setAudioType(findAudioType(request.audioTypeId(), principal.getCompanyId()));
        if (request.projectionTypeId() != null)
            showtime.setProjectionType(findProjectionType(request.projectionTypeId(), principal.getCompanyId()));
        if (request.sessionLanguageId() != null)
            showtime.setSessionLanguage(findSessionLanguage(request.sessionLanguageId(), principal.getCompanyId()));
        if (request.statusId() != null)
            showtime.setStatus(findSessionStatus(request.statusId(), principal.getCompanyId()));
        if (request.baseTicketPrice() != null)
            showtime.setBaseTicketPrice(request.baseTicketPrice());

        showtimeRepository.save(showtime);
        return toResponse(showtime);
    }

    @Transactional(readOnly = true)
    public List<com.frame24.api.operations.application.dto.ShowtimeDashboardResponse> listDashboard(Long complexId,
                                                                                                    Instant start, Instant end, UserPrincipal principal) {
        return vShowtimeAvailabilityRepository.findByCinemaComplexIdAndDateRange(complexId, start, end)
                .stream()
                .filter(v -> v.getCompanyId().equals(principal.getCompanyId()))
                .map(this::toDashboardResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShowtimeResponse> listByComplexAndDateRange(Long complexId, Instant start, Instant end,
                                                            UserPrincipal principal) {
        // TODO: Validate complex belongs to company if necessary, or filter by company
        // in query
        return showtimeRepository.findByCinemaComplexAndDateRange(complexId, start, end)
                .stream()
                .filter(s -> s.getCinemaComplex().getCompanyId().equals(principal.getCompanyId()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        ShowtimeSchedule showtime = findByIdAndCompany(id, principal.getCompanyId());

        if (showtime.getSoldSeats() > 0) {
            throw new ValidationException("Não é possível deletar uma sessão com ingressos vendidos");
        }

        showtimeRepository.delete(showtime);
    }

    private void validateTimes(Instant start, Instant end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Horário de início deve ser anterior ao término");
        }
    }

    private void validateOverlap(Long roomId, Instant start, Instant end, Long excludeShowtimeId) {
        List<ShowtimeSchedule> conflicts = showtimeRepository.findConflictingShowtimes(roomId, start, end);
        boolean hasConflict = conflicts.stream()
                .anyMatch(s -> excludeShowtimeId == null || !s.getId().equals(excludeShowtimeId));

        if (hasConflict) {
            throw new ValidationException("Existe conflito de horário com outra sessão nesta sala");
        }
    }

    private ShowtimeSchedule findByIdAndCompany(Long id, Long companyId) {
        return showtimeRepository.findById(id)
                .filter(s -> s.getCinemaComplex().getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Sessão não encontrada"));
    }

    // Helper finders for types
    private AudioType findAudioType(Long id, Long companyId) { // ... implementation using repo ...
        return audioTypeRepository.findById(id)
                .filter(a -> a.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Tipo de áudio não encontrado: " + id));
    }

    private ProjectionType findProjectionType(Long id, Long companyId) {
        return projectionTypeRepository.findById(id)
                .filter(p -> p.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Tipo de projeção não encontrado: " + id));
    }

    private SessionLanguage findSessionLanguage(Long id, Long companyId) {
        return sessionLanguageRepository.findById(id)
                .filter(l -> l.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Idioma não encontrado: " + id));
    }

    private SessionStatus findSessionStatus(Long id, Long companyId) {
        return sessionStatusRepository.findById(id)
                .filter(s -> s.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Status de sessão não encontrado: " + id));
    }

    private ShowtimeResponse toResponse(ShowtimeSchedule s) {
        return new ShowtimeResponse(
                s.getId(),
                s.getCinemaComplex().getId(),
                s.getCinemaComplex().getName(),
                s.getRoom().getId(),
                s.getRoom().getName(),
                s.getMovieId(),
                s.getStartTime(),
                s.getEndTime(),
                s.getAudioType() != null
                        ? new ShowtimeResponse.TypeInfo(s.getAudioType().getId(), s.getAudioType().getName())
                        : null,
                s.getProjectionType() != null
                        ? new ShowtimeResponse.TypeInfo(s.getProjectionType().getId(), s.getProjectionType().getName())
                        : null,
                s.getSessionLanguage() != null
                        ? new ShowtimeResponse.LanguageInfo(s.getSessionLanguage().getId(),
                        s.getSessionLanguage().getName(), s.getSessionLanguage().getAbbreviation())
                        : null,
                s.getStatus() != null
                        ? new ShowtimeResponse.StatusInfo(s.getStatus().getId(), s.getStatus().getName(),
                        s.getStatus().getAllowsModification())
                        : null,
                s.getAvailableSeats(),
                s.getBaseTicketPrice(),
                s.getCreatedAt());
    }

    @Transactional
    public void cancel(Long id, UserPrincipal principal) {
        ShowtimeSchedule showtime = findByIdAndCompany(id, principal.getCompanyId());

        // Find 'Cancelled' status
        SessionStatus cancelledStatus = sessionStatusRepository.findAll().stream()
                .filter(s -> s.getCompanyId().equals(principal.getCompanyId())
                        && "Cancelled".equalsIgnoreCase(s.getName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Status 'Cancelled' não encontrado"));

        if (showtime.getSoldSeats() > 0) {
            // TODO: Decide business rule. For now, allow cancel but warning logs.
            log.warn("Cancelando sessão {} com {} ingressos vendidos", id, showtime.getSoldSeats());
        }

        showtime.setStatus(cancelledStatus);
        showtimeRepository.save(showtime);
    }

    @Transactional(readOnly = true)
    public com.frame24.api.operations.application.dto.FinancialPreviewResponse getFinancialPreview(Long id,
                                                                                                   UserPrincipal principal) {
        VShowtimeAvailability view = vShowtimeAvailabilityRepository.findById(id)
                .filter(v -> v.getCompanyId().equals(principal.getCompanyId()))
                .orElseThrow(() -> new NotFoundException("Sessão não encontrada"));

        BigDecimal estimatedRevenue = view.getBaseTicketPrice().multiply(BigDecimal.valueOf(view.getSeatsSold()));

        return new com.frame24.api.operations.application.dto.FinancialPreviewResponse(
                view.getShowtimeId(),
                view.getTotalSeats(),
                view.getSeatsSold(),
                view.getSeatsAvailable(),
                view.getOccupancyPercentage(),
                view.getBaseTicketPrice(),
                estimatedRevenue);
    }

    @Transactional(readOnly = true)
    public List<com.frame24.api.operations.application.dto.SeatMapResponse> getSeatMap(Long id,
                                                                                       UserPrincipal principal) {
        ShowtimeSchedule showtime = findByIdAndCompany(id, principal.getCompanyId());

        // 1. Get all physical seats for the room
        List<Seat> physicalSeats = seatRepository.findByRoomIdOrderByRowAndColumn(showtime.getRoom().getId());

        // 2. Get all session-specific seat statuses (sold, blocked, reserved)
        List<SessionSeatStatus> sessionStatuses = sessionSeatStatusRepository.findByShowtime_Id(id);

        // 3. Merge logic
        return physicalSeats.stream().map(seat -> {
            String status = "AVAILABLE";

            // Check if seat is physically active
            if (!seat.getActive()) {
                status = "MAINTENANCE";
            } else {
                // Check session status overlap
                var sessionStatus = sessionStatuses.stream()
                        .filter(ss -> ss.getSeat().getId().equals(seat.getId()))
                        .findFirst();

                if (sessionStatus.isPresent()) {
                    // Decide status based on sessionStatus presence (Sold/Reserved logic to be
                    // refined later)
                    // For now, if record exists, assume SOLD or RESERVED based on fields (e.g.
                    // saleId)
                    if (sessionStatus.get().getSaleId() != null) {
                        status = "SOLD";
                    } else if (sessionStatus.get().getReservationUuid() != null) { // Simple reservation check
                        status = "BLOCKED"; // Or RESERVED
                    }
                }
            }

            return new com.frame24.api.operations.application.dto.SeatMapResponse(
                    seat.getId(),
                    seat.getSeatCode(),
                    seat.getRowCode(),
                    seat.getColumnNumber(),
                    seat.getSeatType() != null ? seat.getSeatType().getName() : "Standard",
                    seat.getPositionX(),
                    seat.getPositionY(),
                    status,
                    seat.getAccessible());
        }).toList();
    }

    private com.frame24.api.operations.application.dto.ShowtimeDashboardResponse toDashboardResponse(
            VShowtimeAvailability v) {
        return new com.frame24.api.operations.application.dto.ShowtimeDashboardResponse(
                v.getShowtimeId(),
                v.getCinemaComplexId(),
                v.getRoomId(),
                v.getRoomName(),
                v.getMovieId(),
                v.getMovieTitle(),
                v.getStartTime(),
                v.getEndTime(),
                v.getTotalSeats(),
                v.getSeatsSold(),
                v.getSeatsAvailable(),
                v.getOccupancyPercentage(),
                v.getSessionStatus(),
                v.getBaseTicketPrice());
    }
}
