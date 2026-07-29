package com.negocio.controlventas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.negocio.controlventas.model.Cobrador;
import com.negocio.controlventas.repository.CobradorRepository;

@Service
public class CobradorService {

    private final CobradorRepository cobradorRepository;

    public CobradorService(
            CobradorRepository cobradorRepository) {

        this.cobradorRepository = cobradorRepository;
    }

    public List<Cobrador> listarCobradores() {
        return cobradorRepository.findAll();
    }

    public List<Cobrador> listarCobradoresActivos() {

        return cobradorRepository
                .findByActivoTrueOrderByApellidoPaternoAscNombresAsc();
    }

    public Cobrador buscarCobradorPorId(
            Long idCobrador) {

        return cobradorRepository
                .findById(idCobrador)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe el cobrador con el ID "
                                        + idCobrador));
    }

    @Transactional
    public Cobrador guardarCobrador(
            Cobrador cobrador) {

        validarCobrador(cobrador);

        String dniLimpio =
                cobrador.getDni().trim();

        if (cobradorRepository
                .existsByDni(dniLimpio)) {

            throw new IllegalArgumentException(
                    "Ya existe un cobrador con el DNI "
                            + dniLimpio);
        }

        cobrador.setNombres(
                cobrador.getNombres().trim());

        cobrador.setApellidoPaterno(
                cobrador.getApellidoPaterno().trim());

        if (cobrador.getApellidoMaterno() != null) {
            cobrador.setApellidoMaterno(
                    cobrador.getApellidoMaterno().trim());
        }

        cobrador.setDni(dniLimpio);
        cobrador.setCelular(
                cobrador.getCelular().trim());

        cobrador.setCodigoCobrador(null);
        cobrador.setActivo(true);

        Cobrador cobradorGuardado =
                cobradorRepository.save(cobrador);

        String codigoGenerado = String.format(
                "COB-%05d",
                cobradorGuardado.getIdCobrador());

        cobradorGuardado.setCodigoCobrador(
                codigoGenerado);

        return cobradorRepository.save(
                cobradorGuardado);
    }

    private void validarCobrador(
            Cobrador cobrador) {

        if (cobrador.getNombres() == null
                || cobrador.getNombres().isBlank()) {

            throw new IllegalArgumentException(
                    "Los nombres son obligatorios");
        }

        if (cobrador.getApellidoPaterno() == null
                || cobrador.getApellidoPaterno().isBlank()) {

            throw new IllegalArgumentException(
                    "El apellido paterno es obligatorio");
        }

        if (cobrador.getDni() == null
                || !cobrador.getDni()
                        .trim()
                        .matches("\\d{8}")) {

            throw new IllegalArgumentException(
                    "El DNI debe contener exactamente 8 números");
        }

        if (cobrador.getCelular() == null
                || !cobrador.getCelular()
                        .trim()
                        .matches("\\d{9}")) {

            throw new IllegalArgumentException(
                    "El celular debe contener exactamente 9 números");
        }
    }
}