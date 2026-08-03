package com.negocio.controlventas.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import com.negocio.controlventas.repository.CuotaPrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import com.negocio.controlventas.repository.PagoPrestamoRepository;
import com.negocio.controlventas.model.CuotaPrestamo;
import com.negocio.controlventas.dto.PrestamoRequest;
import com.negocio.controlventas.model.Cliente;
import com.negocio.controlventas.model.Prestamo;
import com.negocio.controlventas.repository.ClienteRepository;
import com.negocio.controlventas.repository.PrestamoRepository;
import com.negocio.controlventas.dto.PagoPrestamoRequest;
import com.negocio.controlventas.model.PagoPrestamo;

@Service
public class PrestamoService {

    private static final Set<String> MODALIDADES_PERMITIDAS = Set.of(
            "DIARIO",
            "SEMANAL",
            "QUINCENAL",
            "MENSUAL");

    private final PrestamoRepository prestamoRepository;
    private final CuotaPrestamoRepository cuotaPrestamoRepository;
    private final ClienteRepository clienteRepository;
    private final PagoPrestamoRepository pagoPrestamoRepository;

    public PrestamoService(
            PrestamoRepository prestamoRepository,
            ClienteRepository clienteRepository,
            CuotaPrestamoRepository cuotaPrestamoRepository,
            PagoPrestamoRepository pagoPrestamoRepository) {
        this.prestamoRepository = prestamoRepository;
        this.clienteRepository = clienteRepository;
        this.cuotaPrestamoRepository = cuotaPrestamoRepository;
        this.pagoPrestamoRepository = pagoPrestamoRepository;
    }

    public List<Prestamo> listarPrestamos() {
        return prestamoRepository
                .findAllByOrderByFechaRegistroDesc();
    }

    public List<CuotaPrestamo> listarCuotas(
            Long idPrestamo) {
        if (!prestamoRepository.existsById(idPrestamo)) {
            throw new IllegalArgumentException(
                    "El préstamo seleccionado no existe.");
        }

        return cuotaPrestamoRepository
                .findByPrestamo_IdPrestamoOrderByNumeroCuotaAsc(
                        idPrestamo);
    }

    public List<PagoPrestamo> listarPagos(
            Long idPrestamo) {
        if (!prestamoRepository.existsById(idPrestamo)) {
            throw new IllegalArgumentException(
                    "El préstamo seleccionado no existe.");
        }

        return pagoPrestamoRepository
                .findByPrestamo_IdPrestamoOrderByFechaRegistroDesc(
                        idPrestamo);
    }

    public List<PagoPrestamo> listarTodosLosPagos() {
        return pagoPrestamoRepository
                .findAllByOrderByFechaRegistroDesc();
    }

    @Transactional
    public void eliminarPrestamo(Long idPrestamo) {
        Prestamo prestamo = prestamoRepository
                .findById(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El préstamo seleccionado no existe."));

        pagoPrestamoRepository
                .deleteByPrestamo_IdPrestamo(idPrestamo);

        cuotaPrestamoRepository
                .deleteByPrestamo_IdPrestamo(idPrestamo);

        prestamoRepository.delete(prestamo);
    }

    @Transactional
    public PagoPrestamo registrarPago(
            Long idPrestamo,
            PagoPrestamoRequest request) {
        if (request == null ||
                request.montoPago() == null ||
                request.montoPago()
                        .compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto del pago debe ser mayor que cero.");
        }

        Prestamo prestamo = prestamoRepository
                .findById(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El préstamo seleccionado no existe."));

        if ("FINALIZADO".equalsIgnoreCase(
                prestamo.getEstado())) {
            throw new IllegalArgumentException(
                    "Este préstamo ya está completamente pagado.");
        }

        BigDecimal montoPago = request
                .montoPago()
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal saldoAnterior = prestamo
                .getSaldoPendiente()
                .setScale(2, RoundingMode.HALF_UP);

        if (montoPago.compareTo(saldoAnterior) > 0) {
            throw new IllegalArgumentException(
                    "El pago no puede ser mayor que el saldo pendiente.");
        }

        List<CuotaPrestamo> cuotas = cuotaPrestamoRepository
                .findByPrestamo_IdPrestamoOrderByNumeroCuotaAsc(
                        idPrestamo);

        if (cuotas.isEmpty()) {
            throw new IllegalArgumentException(
                    "Este préstamo no tiene cuotas generadas.");
        }

        BigDecimal montoDisponible = montoPago;

        for (CuotaPrestamo cuota : cuotas) {
            if (montoDisponible.compareTo(
                    BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal saldoCuota = cuota
                    .getSaldoPendiente()
                    .setScale(2, RoundingMode.HALF_UP);

            if (saldoCuota.compareTo(
                    BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal montoAplicado = montoDisponible.min(saldoCuota);

            BigDecimal pagadoActual = cuota.getMontoPagado() == null
                    ? BigDecimal.ZERO
                    : cuota.getMontoPagado();

            BigDecimal nuevoMontoPagado = pagadoActual
                    .add(montoAplicado)
                    .setScale(
                            2,
                            RoundingMode.HALF_UP);

            BigDecimal nuevoSaldoCuota = saldoCuota
                    .subtract(montoAplicado)
                    .setScale(
                            2,
                            RoundingMode.HALF_UP);

            cuota.setMontoPagado(nuevoMontoPagado);
            cuota.setSaldoPendiente(nuevoSaldoCuota);

            if (nuevoSaldoCuota.compareTo(
                    BigDecimal.ZERO) == 0) {
                cuota.setEstado("PAGADA");
            } else {
                cuota.setEstado("PARCIAL");
            }

            cuotaPrestamoRepository.save(cuota);

            montoDisponible = montoDisponible
                    .subtract(montoAplicado)
                    .setScale(
                            2,
                            RoundingMode.HALF_UP);
        }

        BigDecimal saldoPosterior = saldoAnterior
                .subtract(montoPago)
                .setScale(2, RoundingMode.HALF_UP);

        prestamo.setSaldoPendiente(saldoPosterior);

        if (saldoPosterior.compareTo(
                BigDecimal.ZERO) == 0) {
            prestamo.setEstado("FINALIZADO");
        }

        prestamoRepository.save(prestamo);

        PagoPrestamo pago = new PagoPrestamo();

        pago.setPrestamo(prestamo);
        pago.setMontoPago(montoPago);
        pago.setFechaPago(
                request.fechaPago() != null
                        ? request.fechaPago()
                        : LocalDate.now());
        pago.setSaldoAnterior(saldoAnterior);
        pago.setSaldoPosterior(saldoPosterior);
        pago.setObservaciones(
                limpiarTexto(request.observaciones()));

        return pagoPrestamoRepository.save(pago);
    }

    @Transactional
    public Prestamo guardarPrestamo(
            PrestamoRequest request) {
        validarRequest(request);

        Cliente cliente = clienteRepository
                .findById(request.idCliente())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El cliente seleccionado no existe."));

        if (!Boolean.TRUE.equals(cliente.getActivo())) {
            throw new IllegalArgumentException(
                    "El cliente seleccionado está inactivo.");
        }

        BigDecimal capital = request
                .montoCapital()
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal porcentaje = request
                .porcentajeInteres()
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal interes = capital
                .multiply(porcentaje)
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP);

        BigDecimal deudaTotal = capital
                .add(interes)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal montoCuota = request
                .montoCuota()
                .setScale(2, RoundingMode.HALF_UP);

        if (montoCuota.compareTo(deudaTotal) > 0) {
            throw new IllegalArgumentException(
                    "La cuota no puede ser mayor que la deuda total.");
        }

        Prestamo prestamo = new Prestamo();

        prestamo.setCliente(cliente);
        prestamo.setMontoCapital(capital);
        prestamo.setPorcentajeInteres(porcentaje);
        prestamo.setMontoInteres(interes);
        prestamo.setDeudaTotal(deudaTotal);
        prestamo.setSaldoPendiente(deudaTotal);
        prestamo.setModalidadPago(
                request.modalidadPago()
                        .trim()
                        .toUpperCase());
        prestamo.setMontoCuota(montoCuota);
        prestamo.setFechaDesembolso(
                request.fechaDesembolso());
        prestamo.setFechaPrimerPago(
                request.fechaPrimerPago());
        prestamo.setEstado("ACTIVO");
        prestamo.setObservaciones(
                limpiarTexto(request.observaciones()));

        Prestamo prestamoGuardado = prestamoRepository.save(prestamo);

        prestamoGuardado.setNumeroPrestamo(
                String.format(
                        "PRE-%05d",
                        prestamoGuardado.getIdPrestamo()));

        Prestamo prestamoFinal = prestamoRepository.save(prestamoGuardado);

        generarCuotasPrestamo(prestamoFinal);

        return prestamoRepository.save(prestamoFinal);
    }

    private void validarRequest(
            PrestamoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Los datos del préstamo son obligatorios.");
        }

        if (request.idCliente() == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un cliente.");
        }

        if (request.montoCapital() == null ||
                request.montoCapital()
                        .compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El capital debe ser mayor que cero.");
        }

        if (request.porcentajeInteres() == null ||
                request.porcentajeInteres()
                        .compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El porcentaje de interés debe ser mayor que cero.");
        }

        if (request.montoCuota() == null ||
                request.montoCuota()
                        .compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto de la cuota debe ser mayor que cero.");
        }

        if (request.modalidadPago() == null ||
                request.modalidadPago().isBlank()) {
            throw new IllegalArgumentException(
                    "Debes seleccionar una modalidad de pago.");
        }

        String modalidad = request
                .modalidadPago()
                .trim()
                .toUpperCase();

        if (!MODALIDADES_PERMITIDAS.contains(modalidad)) {
            throw new IllegalArgumentException(
                    "La modalidad de pago no es válida.");
        }

        if (request.fechaDesembolso() == null) {
            throw new IllegalArgumentException(
                    "La fecha de desembolso es obligatoria.");
        }

        if (request.fechaPrimerPago() == null) {
            throw new IllegalArgumentException(
                    "La fecha del primer pago es obligatoria.");
        }

        if (request.fechaPrimerPago()
                .isBefore(request.fechaDesembolso())) {
            throw new IllegalArgumentException(
                    "El primer pago no puede ser anterior al desembolso.");
        }
    }

    private void generarCuotasPrestamo(
            Prestamo prestamo) {
        BigDecimal saldoRestante = prestamo.getDeudaTotal();

        BigDecimal cuotaBase = prestamo.getMontoCuota();

        LocalDate fechaCuota = prestamo.getFechaPrimerPago();

        int numeroCuota = 1;

        while (saldoRestante.compareTo(
                BigDecimal.ZERO) > 0) {
            BigDecimal montoCuota = saldoRestante.min(cuotaBase)
                    .setScale(
                            2,
                            RoundingMode.HALF_UP);

            CuotaPrestamo cuota = new CuotaPrestamo();

            cuota.setPrestamo(prestamo);
            cuota.setNumeroCuota(numeroCuota);
            cuota.setFechaVencimiento(fechaCuota);
            cuota.setMontoCuota(montoCuota);
            cuota.setMontoPagado(BigDecimal.ZERO);
            cuota.setSaldoPendiente(montoCuota);
            cuota.setEstado("PENDIENTE");

            cuotaPrestamoRepository.save(cuota);

            saldoRestante = saldoRestante
                    .subtract(montoCuota)
                    .setScale(
                            2,
                            RoundingMode.HALF_UP);

            prestamo.setFechaVencimiento(fechaCuota);

            fechaCuota = calcularSiguienteFecha(
                    fechaCuota,
                    prestamo.getModalidadPago());

            numeroCuota++;
        }
    }

    private LocalDate calcularSiguienteFecha(
            LocalDate fechaActual,
            String modalidad) {
        return switch (modalidad) {
            case "DIARIO" ->
                fechaActual.plusDays(1);

            case "SEMANAL" ->
                fechaActual.plusWeeks(1);

            case "QUINCENAL" ->
                fechaActual.plusDays(15);

            case "MENSUAL" ->
                fechaActual.plusMonths(1);

            default ->
                throw new IllegalArgumentException(
                        "La modalidad de pago no es válida.");
        };
    }

    private String limpiarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}