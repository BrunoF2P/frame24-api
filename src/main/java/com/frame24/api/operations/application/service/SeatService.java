package com.frame24.api.operations.application.service;

import com.frame24.api.common.exception.NotFoundException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.*;
import com.frame24.api.operations.domain.Room;
import com.frame24.api.operations.domain.Seat;
import com.frame24.api.operations.domain.SeatType;
import com.frame24.api.operations.infrastructure.repository.RoomRepository;
import com.frame24.api.operations.infrastructure.repository.SeatRepository;
import com.frame24.api.operations.infrastructure.repository.SeatTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service para gerenciamento de assentos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final RoomRepository roomRepository;
    private final SeatTypeRepository seatTypeRepository;

    /**
     * Cria um novo assento individual.
     */
    @Transactional
    public SeatResponse create(CreateSeatRequest request, UserPrincipal principal) {
        log.info("Criando assento individual: room={}, code={}", request.roomId(), request.seatCode());

        Room room = findRoomByIdAndCompany(request.roomId(), principal.getCompanyId());

        if (seatRepository.existsByRoom_IdAndSeatCode(room.getId(), request.seatCode())) {
            throw new ValidationException("Já existe um assento com este código nesta sala");
        }

        Seat seat = new Seat();
        seat.setRoom(room);
        seat.setSeatCode(request.seatCode());
        seat.setRowCode(request.rowCode());
        seat.setColumnNumber(request.columnNumber());
        seat.setPositionX(request.positionX());
        seat.setPositionY(request.positionY());
        seat.setAccessible(request.accessible() != null ? request.accessible() : false);
        seat.setActive(true);
        seat.setCreatedAt(Instant.now());

        if (request.seatTypeId() != null) {
            SeatType seatType = findSeatTypeByIdAndCompany(request.seatTypeId(), principal.getCompanyId());
            seat.setSeatType(seatType);
        }

        seatRepository.save(seat);

        // Atualizar capacidade da sala
        updateRoomCapacity(room);

        return toResponse(seat);
    }

    /**
     * Cria assentos em lote (matriz).
     */
    @Transactional
    public List<SeatResponse> createBatch(BatchCreateSeatsRequest request, UserPrincipal principal) {
        log.info("Criando assentos em lote: room={}, rows={}, cols={}", request.roomId(), request.rows(),
                request.columns());

        Room room = findRoomByIdAndCompany(request.roomId(), principal.getCompanyId());

        // Setup SeatType se fornecido
        SeatType defaultSeatType = null;
        if (request.defaultSeatTypeId() != null) {
            defaultSeatType = findSeatTypeByIdAndCompany(request.defaultSeatTypeId(), principal.getCompanyId());
        }

        List<Seat> newSeats = new ArrayList<>();
        int startCol = request.startColumnNumber() != null ? request.startColumnNumber() : 1;

        for (int r = 0; r < request.rows(); r++) {
            String rowCode = generateRowCode(r, request.rows(), request.rowNamingPattern());

            for (int c = 0; c < request.columns(); c++) {
                int colNum = startCol + c;
                String seatCode = rowCode + colNum;

                // Skip se já existe
                if (seatRepository.existsByRoom_IdAndSeatCode(room.getId(), seatCode)) {
                    continue;
                }

                Seat seat = new Seat();
                seat.setRoom(room);
                seat.setSeatCode(seatCode);
                seat.setRowCode(rowCode);
                seat.setColumnNumber(colNum);
                seat.setSeatType(defaultSeatType);

                // Coordenadas básicas para visualização (pode ser ajustado depois)
                // Assumindo grid simples: X aumenta com coluna, Y aumenta com fileira
                seat.setPositionX(c * 50); // 50px de espaçamento
                seat.setPositionY(r * 50);

                seat.setAccessible(false);
                seat.setActive(true);
                seat.setCreatedAt(Instant.now());

                newSeats.add(seat);
            }
        }

        if (newSeats.isEmpty()) {
            throw new ValidationException("Nenhum assento novo foi gerado (verifique se já existem)");
        }

        seatRepository.saveAll(newSeats);

        // Atualizar capacidade da sala e totais
        updateRoomCapacity(room);
        updateRoomDimensions(room, request.rows(), request.columns());

        log.info("Criados {} assentos na sala {}", newSeats.size(), room.getId());

        return newSeats.stream().map(this::toResponse).toList();
    }

    /**
     * Lista assentos da sala.
     */
    @Transactional(readOnly = true)
    public List<SeatResponse> listByRoom(Long roomId, UserPrincipal principal) {
        Room room = findRoomByIdAndCompany(roomId, principal.getCompanyId());
        return seatRepository.findByRoomIdOrderByRowAndColumn(room.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Atualiza um assento.
     */
    @Transactional
    public SeatResponse update(Long id, UpdateSeatRequest request, UserPrincipal principal) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assento não encontrado"));

        // Validar propriedade da sala/empresa
        if (!seat.getRoom().getCinemaComplex().getCompanyId().equals(principal.getCompanyId())) {
            throw new NotFoundException("Assento não encontrado"); // Security obscuration
        }

        if (request.seatCode() != null && !request.seatCode().equals(seat.getSeatCode())) {
            // Verificar duplicidade
            if (seatRepository.existsByRoom_IdAndSeatCode(seat.getRoom().getId(), request.seatCode())) {
                throw new ValidationException("Já existe um assento com este código nesta sala");
            }
            seat.setSeatCode(request.seatCode());
        }

        if (request.rowCode() != null)
            seat.setRowCode(request.rowCode());
        if (request.columnNumber() != null)
            seat.setColumnNumber(request.columnNumber());
        if (request.positionX() != null)
            seat.setPositionX(request.positionX());
        if (request.positionY() != null)
            seat.setPositionY(request.positionY());
        if (request.accessible() != null)
            seat.setAccessible(request.accessible());

        if (request.active() != null) {
            seat.setActive(request.active());
            // Se mudou status ativo, recomputar capacidade
            updateRoomCapacity(seat.getRoom());
        }

        if (request.seatTypeId() != null) {
            SeatType seatType = findSeatTypeByIdAndCompany(request.seatTypeId(), principal.getCompanyId());
            seat.setSeatType(seatType);
        }

        seatRepository.save(seat);
        return toResponse(seat);
    }

    /**
     * Deleta um assento.
     */
    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assento não encontrado"));

        if (!seat.getRoom().getCinemaComplex().getCompanyId().equals(principal.getCompanyId())) {
            throw new NotFoundException("Assento não encontrado");
        }

        Room room = seat.getRoom();
        seatRepository.delete(seat);
        seatRepository.delete(seat);
        updateRoomCapacity(room);
    }

    /**
     * Atualiza status (ativo/inativo) de assentos em lote.
     */
    @Transactional
    public void updateBatchStatus(BatchUpdateSeatStatusRequest request, UserPrincipal principal) {
        Room room = findRoomByIdAndCompany(request.roomId(), principal.getCompanyId());

        List<Seat> seats = seatRepository.findAllById(request.seatIds());

        // Validate all seats belong to the room
        boolean allBelongToRoom = seats.stream().allMatch(s -> s.getRoom().getId().equals(room.getId()));
        if (!allBelongToRoom) {
            throw new ValidationException("Alguns assentos não pertencem à sala especificada");
        }

        seats.forEach(seat -> seat.setActive(request.active()));
        seatRepository.saveAll(seats);

        updateRoomCapacity(room);
        log.info("Atualizados {} assentos para active={} na sala {}", seats.size(), request.active(), room.getId());
    }

    // Auxiliares

    private String generateRowCode(int rowIndex, int totalRows, BatchCreateSeatsRequest.RowNamingPattern pattern) {
        if (pattern == BatchCreateSeatsRequest.RowNamingPattern.NUMERIC) {
            return String.valueOf(rowIndex + 1);
        }

        // ALPHABETIC logic (A, B, ..., Z, AA, AB...)
        // Simples A-Z para MVP, pode expandir para AA depois se necessário
        // Se REVERSE, inverte index
        int index = (pattern == BatchCreateSeatsRequest.RowNamingPattern.REVERSE_ALPHABETIC)
                ? (totalRows - 1 - rowIndex)
                : rowIndex;

        return getCharForNumber(index);
    }

    private String getCharForNumber(int i) {
        // Base 0 -> A, 1 -> B
        // TODO: Handle i > 25 (AA, AB...) se precisar de muchas fileiras
        return i >= 0 && i < 26 ? String.valueOf((char) ('A' + i)) : String.valueOf(i);
    }

    private void updateRoomCapacity(Room room) {
        Integer capacity = seatRepository.countActiveSeatsByRoomId(room.getId());
        room.setCapacity(capacity != null ? capacity : 0);
        roomRepository.save(room);
    }

    private void updateRoomDimensions(Room room, Integer rows, Integer cols) {
        // Apenas atualiza se for primeira vez ou se for maior
        if (room.getTotalRows() == null || room.getTotalRows() < rows) {
            room.setTotalRows(rows);
        }
        if (room.getTotalColumns() == null || room.getTotalColumns() < cols) {
            room.setTotalColumns(cols);
        }
        roomRepository.save(room);
    }

    private Room findRoomByIdAndCompany(Long roomId, Long companyId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Sala não encontrada"));

        if (!room.getCinemaComplex().getCompanyId().equals(companyId)) {
            throw new NotFoundException("Sala não encontrada");
        }
        return room;
    }

    private SeatType findSeatTypeByIdAndCompany(Long id, Long companyId) {
        return seatTypeRepository.findById(id)
                .filter(st -> st.getCompanyId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Tipo de assento não encontrado"));
    }

    private SeatResponse toResponse(Seat seat) {
        SeatResponse.TypeInfo typeInfo = null;
        if (seat.getSeatType() != null) {
            typeInfo = new SeatResponse.TypeInfo(
                    seat.getSeatType().getId(),
                    seat.getSeatType().getName(),
                    seat.getSeatType().getDescription());
        }

        return new SeatResponse(
                seat.getId(),
                seat.getRoom().getId(),
                seat.getSeatCode(),
                seat.getRowCode(),
                seat.getColumnNumber(),
                typeInfo,
                seat.getPositionX(),
                seat.getPositionY(),
                seat.getAccessible(),
                seat.getActive(),
                seat.getCreatedAt());
    }
}
