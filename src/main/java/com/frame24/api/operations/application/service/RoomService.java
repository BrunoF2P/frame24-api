package com.frame24.api.operations.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateRoomRequest;
import com.frame24.api.operations.application.dto.RoomResponse;
import com.frame24.api.operations.application.dto.UpdateRoomRequest;
import com.frame24.api.operations.domain.*;
import com.frame24.api.operations.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

/**
 * Service para gerenciamento de salas de cinema.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final CinemaComplexRepository cinemaComplexRepository;
    private final ProjectionTypeRepository projectionTypeRepository;
    private final AudioTypeRepository audioTypeRepository;
    private final SeatRepository seatRepository;
    private final SeatTypeRepository seatTypeRepository;
    private final ObjectMapper objectMapper;

    /**
     * Cria uma nova sala de cinema.
     */
    @Transactional
    public RoomResponse create(CreateRoomRequest request, UserPrincipal principal) {
        log.info("Criando sala: roomNumber={}, cinemaComplexId={}", request.roomNumber(), request.cinemaComplexId());

        // Buscar e validar complexo
        CinemaComplex complex = cinemaComplexRepository.findById(request.cinemaComplexId())
                .filter(c -> c.getCompanyId().equals(principal.getCompanyId()))
                .orElseThrow(() -> new NotFoundException("Complexo de cinema não encontrado"));

        // Validar número da sala único no complexo
        if (roomRepository.existsByCinemaComplex_IdAndRoomNumber(request.cinemaComplexId(), request.roomNumber())) {
            throw new ValidationException("Já existe uma sala com este número neste complexo");
        }

        Room room = new Room();
        room.setCinemaComplex(complex);
        room.setRoomNumber(request.roomNumber());
        room.setName(request.name());
        room.setCapacity(request.capacity());
        room.setTotalRows(request.totalRows());
        room.setTotalColumns(request.totalColumns());
        room.setRoomDesign(request.roomDesign());
        room.setActive(true);
        room.setCreatedAt(Instant.now());

        // Associar tipo de projeção (opcional)
        if (request.projectionTypeId() != null) {
            ProjectionType projType = projectionTypeRepository.findById(request.projectionTypeId())
                    .filter(pt -> pt.getCompanyId().equals(principal.getCompanyId()))
                    .orElseThrow(() -> new ValidationException("Tipo de projeção não encontrado"));
            room.setProjectionType(projType);
        }

        // Associar tipo de áudio (opcional)
        if (request.audioTypeId() != null) {
            AudioType audioType = audioTypeRepository.findById(request.audioTypeId())
                    .filter(at -> at.getCompanyId().equals(principal.getCompanyId()))
                    .orElseThrow(() -> new ValidationException("Tipo de áudio não encontrado"));
            room.setAudioType(audioType);
        }

        roomRepository.save(room);
        log.info("Sala criada: id={}", room.getId());

        // Processar e criar assentos se fornecidos
        if (request.seats() != null && !request.seats().isEmpty()) {
            List<Seat> seatsToSave = request.seats().stream().map(seatRequest -> {
                Seat seat = new Seat();
                seat.setRoom(room);
                seat.setSeatCode(seatRequest.seatCode());
                seat.setRowCode(seatRequest.rowCode());
                seat.setColumnNumber(seatRequest.columnNumber());
                seat.setPositionX(seatRequest.positionX());
                seat.setPositionY(seatRequest.positionY());
                seat.setAccessible(seatRequest.accessible() != null ? seatRequest.accessible() : false);
                seat.setActive(true);
                seat.setCreatedAt(Instant.now());

                if (seatRequest.seatTypeId() != null) {
                    SeatType seatType = seatTypeRepository.findById(seatRequest.seatTypeId())
                            .filter(st -> st.getCompanyId().equals(principal.getCompanyId()))
                            .orElseThrow(() -> new ValidationException(
                                    "Tipo de assento não encontrado: " + seatRequest.seatTypeId()));
                    seat.setSeatType(seatType);
                }

                return seat;
            }).toList();

            seatRepository.saveAll(seatsToSave);
            log.info("Criados {} assentos para a sala {}", seatsToSave.size(), room.getId());
        }

        return toResponse(room);
    }

    /**
     * Lista todas as salas de um complexo.
     */
    @Transactional(readOnly = true)
    public List<RoomResponse> listByComplex(Long cinemaComplexId, UserPrincipal principal) {
        log.debug("Listando salas do complexo: cinemaComplexId={}", cinemaComplexId);

        // Validar acesso ao complexo
        cinemaComplexRepository.findById(cinemaComplexId)
                .filter(c -> c.getCompanyId().equals(principal.getCompanyId()))
                .orElseThrow(() -> new NotFoundException("Complexo de cinema não encontrado"));

        return roomRepository.findByCinemaComplex_Id(cinemaComplexId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Lista todas as salas da empresa.
     */
    @Transactional(readOnly = true)
    public List<RoomResponse> listAll(UserPrincipal principal) {
        log.debug("Listando todas as salas da empresa: companyId={}", principal.getCompanyId());

        return roomRepository.findByCompanyId(principal.getCompanyId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Busca sala por ID.
     */
    @Transactional(readOnly = true)
    public RoomResponse getById(Long id, UserPrincipal principal) {
        log.debug("Buscando sala: id={}", id);

        Room room = findByIdAndCompany(id, principal.getCompanyId());
        return toResponse(room);
    }

    /**
     * Atualiza uma sala.
     */
    @Transactional
    public RoomResponse update(Long id, UpdateRoomRequest request, UserPrincipal principal) {
        log.info("Atualizando sala: id={}", id);

        Room room = findByIdAndCompany(id, principal.getCompanyId());

        // Validar número da sala se estiver sendo alterado
        if (request.roomNumber() != null && !request.roomNumber().equals(room.getRoomNumber())) {
            if (roomRepository.existsByCinemaComplex_IdAndRoomNumber(room.getCinemaComplex().getId(),
                    request.roomNumber())) {
                throw new ValidationException("Já existe uma sala com este número neste complexo");
            }
            room.setRoomNumber(request.roomNumber());
        }

        if (request.name() != null) {
            room.setName(request.name());
        }

        if (request.capacity() != null) {
            room.setCapacity(request.capacity());
        }

        if (request.active() != null) {
            room.setActive(request.active());
        }

        if (request.totalRows() != null) {
            room.setTotalRows(request.totalRows());
        }

        if (request.totalColumns() != null) {
            room.setTotalColumns(request.totalColumns());
        }

        if (request.roomDesign() != null) {
            room.setRoomDesign(request.roomDesign());
        }

        // Atualizar tipo de projeção
        if (request.projectionTypeId() != null) {
            ProjectionType projType = projectionTypeRepository.findById(request.projectionTypeId())
                    .filter(pt -> pt.getCompanyId().equals(principal.getCompanyId()))
                    .orElseThrow(() -> new ValidationException("Tipo de projeção não encontrado"));
            room.setProjectionType(projType);
        }

        // Atualizar tipo de áudio
        if (request.audioTypeId() != null) {
            AudioType audioType = audioTypeRepository.findById(request.audioTypeId())
                    .filter(at -> at.getCompanyId().equals(principal.getCompanyId()))
                    .orElseThrow(() -> new ValidationException("Tipo de áudio não encontrado"));
            room.setAudioType(audioType);
        }

        roomRepository.save(room);
        log.info("Sala atualizada: id={}", id);

        return toResponse(room);
    }

    /**
     * Deleta uma sala.
     */
    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        log.info("Deletando sala: id={}", id);

        Room room = findByIdAndCompany(id, principal.getCompanyId());

        // Verificar se não tem sessões agendadas
        if (!room.getShowtimeSchedules().isEmpty()) {
            throw new ValidationException("Não é possível deletar: sala tem " +
                    room.getShowtimeSchedules().size() + " sessão(ões) agendada(s)");
        }

        roomRepository.delete(room);
        log.info("Sala deletada: id={}", id);
    }

    private Room findByIdAndCompany(Long id, Long companyId) {
        return roomRepository.findById(id)
                .filter(r -> r.getCinemaComplex().getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Sala não encontrada"));
    }

    private RoomResponse toResponse(Room room) {
        RoomResponse.TypeInfo projTypeInfo = null;
        if (room.getProjectionType() != null) {
            projTypeInfo = new RoomResponse.TypeInfo(
                    room.getProjectionType().getId(),
                    room.getProjectionType().getName());
        }

        RoomResponse.TypeInfo audioTypeInfo = null;
        if (room.getAudioType() != null) {
            audioTypeInfo = new RoomResponse.TypeInfo(
                    room.getAudioType().getId(),
                    room.getAudioType().getName());
        }

        Object seatLayoutObj = null;
        if (room.getSeatLayout() != null) {
            seatLayoutObj = objectMapper.readValue(room.getSeatLayout(), Object.class);
        }

        return new RoomResponse(
                room.getId(),
                room.getCinemaComplex().getId(),
                room.getCinemaComplex().getName(),
                room.getRoomNumber(),
                room.getName(),
                room.getCapacity(),
                projTypeInfo,
                audioTypeInfo,
                room.getActive(),
                room.getTotalRows(),
                room.getTotalColumns(),
                room.getRoomDesign(),
                seatLayoutObj,
                room.getCreatedAt());
    }
}
