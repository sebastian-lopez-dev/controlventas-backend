let cobradoresCobranza = [];
let contratosCobranza = [];
let pagosRecientes = [];
let contratoPagoSeleccionado = null;

document.addEventListener(
    "DOMContentLoaded",
    async () => {

        colocarFechasCobranza();

        document
            .getElementById("btnCargarHoja")
            .addEventListener(
                "click",
                cargarHojaCobranza
            );

        document
            .getElementById(
                "buscadorCobranza"
            )
            .addEventListener(
                "input",
                aplicarFiltroCobranza
            );

        document
            .getElementById(
                "tablaHojaCobranza"
            )
            .addEventListener(
                "click",
                controlarAccionHojaCobranza
            );

        document
            .getElementById(
                "btnCerrarPanelPago"
            )
            .addEventListener(
                "click",
                cerrarPanelPago
            );

        document
            .getElementById(
                "btnCancelarPago"
            )
            .addEventListener(
                "click",
                cerrarPanelPago
            );

        document
            .getElementById("formPago")
            .addEventListener(
                "submit",
                guardarPagoCobranza
            );

        document
            .getElementById(
                "cobradorHoja"
            )
            .addEventListener(
                "change",
                actualizarTotalCobradoCobranza
            );

        document
            .getElementById(
                "fechaHojaCobranza"
            )
            .addEventListener(
                "change",
                actualizarTotalCobradoCobranza
            );

        await Promise.all([
            cargarCobradoresCobranza(),
            cargarPagosRecientes()
        ]);
    }
);

function colocarFechasCobranza() {

    const hoy =
        new Date();

    const fechaActual =
        convertirFechaCobranzaInput(
            hoy
        );

    document
        .getElementById(
            "fechaHojaCobranza"
        )
        .value =
        fechaActual;

    document
        .getElementById(
            "fechaPago"
        )
        .value =
        fechaActual;
}

function convertirFechaCobranzaInput(
    fecha
) {

    const anio =
        fecha.getFullYear();

    const mes =
        String(
            fecha.getMonth() + 1
        ).padStart(2, "0");

    const dia =
        String(
            fecha.getDate()
        ).padStart(2, "0");

    return `${anio}-${mes}-${dia}`;
}

async function cargarCobradoresCobranza() {

    const selector =
        document.getElementById(
            "cobradorHoja"
        );

    selector.innerHTML = `
        <option value="">
            Cargando cobradores...
        </option>
    `;

    try {

        const respuesta =
            await fetch(
                "/api/cobradores/activos"
            );

        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar "
                + "los cobradores."
            );
        }

        cobradoresCobranza =
            await respuesta.json();

        selector.innerHTML = `
            <option value="">
                Selecciona un cobrador
            </option>
        `;

        for (const cobrador
            of cobradoresCobranza) {

            const opcion =
                document.createElement(
                    "option"
                );

            opcion.value =
                cobrador.idCobrador;

            const nombreCompleto =
                obtenerNombreCobrador(
                    cobrador
                );

            const codigo =
                cobrador.codigoCobrador
                || cobrador.codigo
                || `COB-${String(
                    cobrador.idCobrador
                ).padStart(4, "0")}`;

            opcion.textContent =
                `${codigo} - ${nombreCompleto}`;

            selector.appendChild(
                opcion
            );
        }

        if (cobradoresCobranza.length
            === 0) {

            selector.innerHTML = `
                <option value="">
                    No hay cobradores activos
                </option>
            `;
        }

    } catch (error) {

        console.error(error);

        selector.innerHTML = `
            <option value="">
                Error al cargar cobradores
            </option>
        `;

        mostrarMensajeCobranza(
            error.message,
            "error"
        );
    }
}

function obtenerNombreCobrador(
    cobrador
) {

    if (!cobrador) {
        return "Sin cobrador";
    }

    if (cobrador.nombreCompleto) {
        return cobrador.nombreCompleto;
    }

    const nombreCompleto = [
        cobrador.nombres,
        cobrador.apellidos
    ]
        .filter(Boolean)
        .join(" ");

    return nombreCompleto
        || `Cobrador ${cobrador.idCobrador}`;
}

function mostrarMensajeCobranza(
    texto,
    tipo
) {

    const mensaje =
        document.getElementById(
            "mensajeCobranza"
        );

    mensaje.textContent =
        texto;

    mensaje.className =
        tipo === "exito"
            ? "mensaje-sistema mensaje-exito"
            : "mensaje-sistema mensaje-error-producto";

    window.setTimeout(
        () =>
            mensaje.classList.add(
                "oculto"
            ),
        4500
    );
}

function escaparCobranza(
    valor
) {

    const elemento =
        document.createElement(
            "div"
        );

    elemento.textContent =
        valor ?? "";

    return elemento.innerHTML;
}

function formatearDineroCobranza(
    cantidad
) {

    return new Intl.NumberFormat(
        "es-PE",
        {
            style: "currency",
            currency: "PEN"
        }
    ).format(
        Number(cantidad) || 0
    );
}

async function cargarHojaCobranza(
    mostrarConfirmacion = true
) {

    const idCobrador =
        Number(
            document
                .getElementById(
                    "cobradorHoja"
                )
                .value
        );

    const fecha =
        document
            .getElementById(
                "fechaHojaCobranza"
            )
            .value;

    if (!idCobrador) {

        mostrarMensajeCobranza(
            "Selecciona un cobrador.",
            "error"
        );

        return;
    }

    if (!fecha) {

        if (mostrarConfirmacion) {

            mostrarMensajeCobranza(
                "Hoja de cobranza cargada.",
                "exito"
            );
        }

        return;
    }

    const tabla =
        document.getElementById(
            "tablaHojaCobranza"
        );

    const boton =
        document.getElementById(
            "btnCargarHoja"
        );

    const textoOriginal =
        boton.textContent;

    tabla.innerHTML = `
        <tr>
            <td colspan="7" class="tabla-vacia">
                Cargando hoja de cobranza...
            </td>
        </tr>
    `;

    try {

        boton.disabled = true;
        boton.textContent =
            "Cargando...";

        const respuesta =
            await fetch("/api/ventas");

        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar "
                + "los contratos."
            );
        }

        const ventas =
            await respuesta.json();

        const contratosAsignados =
            ventas.filter(venta => {

                const idCobradorVenta =
                    obtenerIdCobradorVenta(
                        venta
                    );

                const estado =
                    venta.estado || "";

                const contratoTerminado =
                    estado === "PAGADA"
                    || estado === "ANULADA"
                    || estado === "CANCELADA";

                return idCobradorVenta
                    === idCobrador
                    && !contratoTerminado;
            });

        contratosCobranza =
            await Promise.all(
                contratosAsignados.map(
                    venta =>
                        agregarProximaCuota(
                            venta
                        )
                )
            );

        contratosCobranza.sort(
            (a, b) => {

                const fechaA =
                    a.proximaCuota
                        ?.fechaVencimiento
                    || "9999-12-31";

                const fechaB =
                    b.proximaCuota
                        ?.fechaVencimiento
                    || "9999-12-31";

                return fechaA.localeCompare(
                    fechaB
                );
            }
        );

        aplicarFiltroCobranza();

        mostrarMensajeCobranza(
            "Hoja de cobranza cargada.",
            "exito"
        );

    } catch (error) {

        console.error(error);

        contratosCobranza = [];

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    No se pudo cargar la hoja.
                </td>
            </tr>
        `;

        actualizarResumenHoja([]);

        mostrarMensajeCobranza(
            error.message,
            "error"
        );

    } finally {

        boton.disabled = false;
        boton.textContent =
            textoOriginal;
    }
}

function obtenerIdCobradorVenta(
    venta
) {

    if (venta.cobrador
        && venta.cobrador.idCobrador) {

        return Number(
            venta.cobrador.idCobrador
        );
    }

    return Number(
        venta.idCobrador || 0
    );
}

async function agregarProximaCuota(
    venta
) {

    try {

        const respuesta =
            await fetch(
                `/api/ventas/${venta.idVenta}/cuotas`
            );

        if (!respuesta.ok) {

            return {
                ...venta,
                proximaCuota: null
            };
        }

        const cuotas =
            await respuesta.json();

        const pendientes =
            cuotas
                .filter(cuota => {

                    const estado =
                        cuota.estado || "";

                    return estado !== "PAGADA"
                        && estado !== "ANULADA";
                })
                .sort(
                    (a, b) =>
                        (
                            a.fechaVencimiento
                            || ""
                        ).localeCompare(
                            b.fechaVencimiento
                            || ""
                        )
                );

        return {
            ...venta,
            proximaCuota:
                pendientes[0] || null
        };

    } catch (error) {

        console.error(error);

        return {
            ...venta,
            proximaCuota: null
        };
    }
}

function aplicarFiltroCobranza() {

    const texto =
        document
            .getElementById(
                "buscadorCobranza"
            )
            .value
            .trim()
            .toLowerCase();

    const filtrados =
        contratosCobranza.filter(
            contrato => {

                const contenido = [
                    obtenerNumeroContratoCobranza(
                        contrato
                    ),
                    obtenerNombreClienteCobranza(
                        contrato
                    ),
                    contrato.modalidadPago
                ]
                    .filter(Boolean)
                    .join(" ")
                    .toLowerCase();

                return contenido.includes(
                    texto
                );
            }
        );

    mostrarHojaCobranza(
        filtrados
    );

    actualizarResumenHoja(
        filtrados
    );
}

function mostrarHojaCobranza(
    contratos
) {

    const tabla =
        document.getElementById(
            "tablaHojaCobranza"
        );

    if (contratos.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    No hay contratos pendientes
                    para este cobrador.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML =
        contratos.map(contrato => {

            const proximaCuota =
                contrato.proximaCuota;

            const montoCuota =
                obtenerSaldoCuota(
                    proximaCuota,
                    contrato
                );

            const saldoContrato =
                Number(
                    contrato.saldoPendiente
                    ?? contrato.saldo
                    ?? 0
                );

            const modalidad =
                contrato.modalidadPago
                || "Sin modalidad";

            const diaPago =
                contrato.diaPago
                    ? ` - ${contrato.diaPago}`
                    : "";

            return `
                <tr>
                    <td>
                        <strong>
                            ${escaparCobranza(
                obtenerNumeroContratoCobranza(
                    contrato
                )
            )}
                        </strong>
                    </td>

                    <td>
                        <div class="cliente-cobranza">
                            <strong>
                                ${escaparCobranza(
                obtenerNombreClienteCobranza(
                    contrato
                )
            )}
                            </strong>

                            <span>
                                ${escaparCobranza(
                obtenerTelefonoClienteCobranza(
                    contrato
                )
            )}
                            </span>
                        </div>
                    </td>

                    <td>
                        ${escaparCobranza(
                modalidad + diaPago
            )}
                    </td>

                    <td>
                        <span class="cuota-cobranza">
                            ${formatearDineroCobranza(
                montoCuota
            )}
                        </span>
                    </td>

                    <td>
                        <span class="saldo-cobranza">
                            ${formatearDineroCobranza(
                saldoContrato
            )}
                        </span>
                    </td>

                    <td>
                        ${formatearFechaCobranza(
                proximaCuota
                    ?.fechaVencimiento
            )}
                    </td>

                    <td>
                        <button
                            type="button"
                            class="boton-registrar-pago"
                            data-accion="registrar-pago"
                            data-id="${contrato.idVenta}"
                        >
                            Registrar pago
                        </button>
                    </td>
                </tr>
            `;
        }).join("");
}

function actualizarResumenHoja(
    contratos
) {

    const totalPendiente =
        contratos.reduce(
            (total, contrato) =>
                total
                + Number(
                    contrato.saldoPendiente
                    ?? contrato.saldo
                    ?? 0
                ),
            0
        );

    document
        .getElementById(
            "totalContratosCobranza"
        )
        .textContent =
        contratos.length;

    document
        .getElementById(
            "totalPendienteCobranza"
        )
        .textContent =
        formatearDineroCobranza(
            totalPendiente
        );
}

function obtenerNumeroContratoCobranza(
    contrato
) {

    return contrato.numeroContrato
        || `CTR-${String(
            contrato.idVenta
        ).padStart(6, "0")}`;
}

function obtenerNombreClienteCobranza(
    contrato
) {

    const cliente =
        contrato.cliente || {};

    if (cliente.nombreCompleto) {
        return cliente.nombreCompleto;
    }

    const nombre = [
        cliente.nombres,
        cliente.apellidoPaterno,
        cliente.apellidoMaterno
    ]
        .filter(Boolean)
        .join(" ");

    return nombre
        || contrato.nombreCliente
        || "Cliente no disponible";
}

function obtenerTelefonoClienteCobranza(
    contrato
) {

    const cliente =
        contrato.cliente || {};

    return cliente.telefono
        || cliente.celular
        || contrato.telefonoCliente
        || "Sin teléfono";
}

function obtenerSaldoCuota(
    cuota,
    contrato
) {

    if (!cuota) {

        return Number(
            contrato.montoCuota
            ?? 0
        );
    }

    return Number(
        cuota.saldoCuota
        ?? cuota.saldo
        ?? cuota.montoProgramado
        ?? cuota.montoCuota
        ?? cuota.monto
        ?? 0
    );
}

function formatearFechaCobranza(
    fechaTexto
) {

    if (!fechaTexto) {
        return "Sin fecha";
    }

    const partes =
        fechaTexto.split("-");

    const fecha =
        new Date(
            Number(partes[0]),
            Number(partes[1]) - 1,
            Number(partes[2])
        );

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            dateStyle: "medium"
        }
    ).format(fecha);
}

function controlarAccionHojaCobranza(
    evento
) {

    const boton =
        evento.target.closest(
            "button[data-accion='registrar-pago']"
        );

    if (!boton) {
        return;
    }

    const idVenta =
        Number(boton.dataset.id);

    abrirPanelRegistrarPago(
        idVenta
    );
}

function abrirPanelRegistrarPago(
    idVenta
) {

    const contrato =
        contratosCobranza.find(
            elemento =>
                elemento.idVenta
                === idVenta
        );

    if (!contrato) {

        mostrarMensajeCobranza(
            "No se encontró el contrato.",
            "error"
        );

        return;
    }

    contratoPagoSeleccionado =
        contrato;

    const numeroContrato =
        obtenerNumeroContratoCobranza(
            contrato
        );

    const cliente =
        obtenerNombreClienteCobranza(
            contrato
        );

    const saldoContrato =
        Number(
            contrato.saldoPendiente
            ?? contrato.saldo
            ?? 0
        );

    const saldoCuota =
        obtenerSaldoCuota(
            contrato.proximaCuota,
            contrato
        );

    document
        .getElementById("formPago")
        .reset();

    document
        .getElementById(
            "idVentaPago"
        )
        .value =
        contrato.idVenta;

    document
        .getElementById(
            "fechaPago"
        )
        .value =
        convertirFechaCobranzaInput(
            new Date()
        );

    const montoSugerido =
        Math.min(
            saldoCuota,
            saldoContrato
        );

    const campoMonto =
        document.getElementById(
            "montoPago"
        );

    campoMonto.value =
        montoSugerido > 0
            ? montoSugerido.toFixed(2)
            : "";

    campoMonto.max =
        saldoContrato.toFixed(2);

    document
        .getElementById(
            "tituloPago"
        )
        .textContent =
        `Registrar pago - ${numeroContrato}`;

    document
        .getElementById(
            "informacionPago"
        )
        .innerHTML = `
            <div class="resumen-contrato-pago">

                <div>
                    <span>Cliente</span>

                    <strong>
                        ${escaparCobranza(
            cliente
        )}
                    </strong>
                </div>

                <div>
                    <span>Cuota pendiente</span>

                    <strong>
                        ${formatearDineroCobranza(
            saldoCuota
        )}
                    </strong>
                </div>

                <div>
                    <span>Saldo del contrato</span>

                    <strong>
                        ${formatearDineroCobranza(
            saldoContrato
        )}
                    </strong>
                </div>

            </div>
        `;

    const panel =
        document.getElementById(
            "panelRegistrarPago"
        );

    panel.classList.remove(
        "oculto"
    );

    panel.scrollIntoView({
        behavior: "smooth"
    });

    window.setTimeout(
        () => campoMonto.focus(),
        400
    );
}

function cerrarPanelPago() {

    document
        .getElementById(
            "panelRegistrarPago"
        )
        .classList.add(
            "oculto"
        );

    document
        .getElementById(
            "formPago"
        )
        .reset();

    document
        .getElementById(
            "idVentaPago"
        )
        .value = "";

    document
        .getElementById(
            "informacionPago"
        )
        .textContent =
        "Selecciona un contrato.";

    contratoPagoSeleccionado =
        null;
}

async function guardarPagoCobranza(
    evento
) {

    evento.preventDefault();

    if (!contratoPagoSeleccionado) {

        mostrarMensajeCobranza(
            "Selecciona un contrato.",
            "error"
        );

        return;
    }

    const idVenta =
        Number(
            document
                .getElementById(
                    "idVentaPago"
                )
                .value
        );

    const idCobrador =
        Number(
            document
                .getElementById(
                    "cobradorHoja"
                )
                .value
        );

    const fechaPago =
        document
            .getElementById(
                "fechaPago"
            )
            .value;

    const monto =
        Number(
            document
                .getElementById(
                    "montoPago"
                )
                .value
        );

    const medioPago =
        document
            .getElementById(
                "medioPago"
            )
            .value;

    const numeroOperacion =
        document
            .getElementById(
                "numeroOperacionPago"
            )
            .value
            .trim();

    const observaciones =
        document
            .getElementById(
                "observacionesPago"
            )
            .value
            .trim();

    const saldoContrato =
        Number(
            contratoPagoSeleccionado
                .saldoPendiente
            ?? contratoPagoSeleccionado
                .saldo
            ?? 0
        );

    if (!idVenta) {

        mostrarMensajeCobranza(
            "El contrato no es válido.",
            "error"
        );

        return;
    }

    if (!idCobrador) {

        mostrarMensajeCobranza(
            "Selecciona un cobrador.",
            "error"
        );

        return;
    }

    if (!fechaPago) {

        mostrarMensajeCobranza(
            "Selecciona la fecha del pago.",
            "error"
        );

        return;
    }

    if (!Number.isFinite(monto)
        || monto <= 0) {

        mostrarMensajeCobranza(
            "El monto debe ser mayor que cero.",
            "error"
        );

        return;
    }

    if (monto > saldoContrato) {

        mostrarMensajeCobranza(
            "El pago no puede superar "
            + "el saldo del contrato.",
            "error"
        );

        return;
    }

    if (!medioPago) {

        mostrarMensajeCobranza(
            "Selecciona el medio de pago.",
            "error"
        );

        return;
    }

    const solicitud = {

        idVenta:
            idVenta,

        idCobrador:
            idCobrador,

        fechaPago:
            fechaPago,

        monto:
            monto,

        medioPago:
            medioPago,

        numeroOperacion:
            numeroOperacion || null,

        observaciones:
            observaciones || null
    };

    const boton =
        document.getElementById(
            "btnGuardarPago"
        );

    const textoOriginal =
        boton.textContent;

    try {

        boton.disabled = true;
        boton.textContent =
            "Registrando pago...";

        const respuesta =
            await fetch(
                "/api/pagos",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(
                            solicitud
                        )
                }
            );

        const textoRespuesta =
            await respuesta.text();

        let resultado = {};

        if (textoRespuesta) {

            try {
                resultado =
                    JSON.parse(
                        textoRespuesta
                    );
            } catch (errorJson) {
                resultado = {};
            }
        }

        if (!respuesta.ok) {

            throw new Error(
                resultado.mensaje
                || "No se pudo registrar el pago."
            );
        }

        const numeroRecibo =
            resultado.numeroRecibo
            || resultado.codigoPago
            || (
                resultado.idPago
                    ? `PAG-${String(
                        resultado.idPago
                    ).padStart(6, "0")}`
                    : "generado"
            );

        cerrarPanelPago();

        mostrarMensajeCobranza(
            `Pago registrado correctamente. `
            + `Recibo: ${numeroRecibo}.`,
            "exito"
        );

        await cargarHojaCobranza(false);
        await cargarPagosRecientes();

    } catch (error) {

        console.error(error);

        mostrarMensajeCobranza(
            error.message,
            "error"
        );

    } finally {

        boton.disabled = false;
        boton.textContent =
            textoOriginal;
    }
}

async function cargarPagosRecientes() {

    const tabla =
        document.getElementById(
            "tablaPagosRecientes"
        );

    tabla.innerHTML = `
        <tr>
            <td colspan="6" class="tabla-vacia">
                Cargando pagos...
            </td>
        </tr>
    `;

    try {

        const respuesta =
            await fetch("/api/pagos");

        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar "
                + "los pagos registrados."
            );
        }

        pagosRecientes =
            await respuesta.json();

        pagosRecientes.sort(
            (a, b) => {

                const idA =
                    Number(a.idPago || 0);

                const idB =
                    Number(b.idPago || 0);

                return idB - idA;
            }
        );

        mostrarPagosRecientes(
            pagosRecientes.slice(0, 50)
        );

        actualizarTotalCobradoCobranza();

    } catch (error) {

        console.error(error);

        pagosRecientes = [];

        tabla.innerHTML = `
            <tr>
                <td colspan="6" class="tabla-vacia">
                    No se pudieron cargar los pagos.
                </td>
            </tr>
        `;

        document
            .getElementById(
                "contadorPagos"
            )
            .textContent =
            "0 pagos";

        actualizarTotalCobradoCobranza();
    }
}

function mostrarPagosRecientes(
    pagos
) {

    const tabla =
        document.getElementById(
            "tablaPagosRecientes"
        );

    document
        .getElementById(
            "contadorPagos"
        )
        .textContent =
        `${pagos.length} `
        + (
            pagos.length === 1
                ? "pago"
                : "pagos"
        );

    if (pagos.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="6" class="tabla-vacia">
                    No hay pagos registrados.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML =
        pagos.map(pago => {

            const venta =
                obtenerVentaDelPago(
                    pago
                );

            const numeroRecibo =
                obtenerNumeroRecibo(
                    pago
                );

            const numeroContrato =
                venta
                    ? obtenerNumeroContratoCobranza(
                        venta
                    )
                    : pago.numeroContrato
                    || "Sin contrato";

            const cliente =
                obtenerNombreClientePago(
                    pago,
                    venta
                );

            const cobrador =
                obtenerCobradorDelPago(
                    pago
                );

            const monto =
                Number(
                    pago.monto
                    ?? pago.montoPago
                    ?? 0
                );

            return `
                <tr>
                    <td>
                        <strong>
                            ${escaparCobranza(
                numeroRecibo
            )}
                        </strong>
                    </td>

                    <td>
                        ${formatearFechaCobranza(
                pago.fechaPago
            )}
                    </td>

                    <td>
                        ${escaparCobranza(
                cliente
            )}
                    </td>

                    <td>
                        ${escaparCobranza(
                numeroContrato
            )}
                    </td>

                    <td>
                        <span class="monto-pago-registrado">
                            ${formatearDineroCobranza(
                monto
            )}
                        </span>
                    </td>

                    <td>
                        ${escaparCobranza(
                obtenerNombreCobrador(
                    cobrador
                )
            )}
                    </td>
                </tr>
            `;
        }).join("");
}

function obtenerVentaDelPago(
    pago
) {

    return pago.venta
        || pago.ventaCredito
        || pago.contrato
        || null;
}

function obtenerCobradorDelPago(
    pago
) {

    return pago.cobrador
        || {
        idCobrador:
            pago.idCobrador,

        nombres:
            pago.nombreCobrador
    };
}

function obtenerNombreClientePago(
    pago,
    venta
) {

    if (venta) {

        return obtenerNombreClienteCobranza(
            venta
        );
    }

    return pago.nombreCliente
        || "Cliente no disponible";
}

function obtenerNumeroRecibo(
    pago
) {

    return pago.numeroRecibo
        || pago.codigoPago
        || `PAG-${String(
            pago.idPago || 0
        ).padStart(6, "0")}`;
}

function actualizarTotalCobradoCobranza() {

    const idCobrador =
        Number(
            document
                .getElementById(
                    "cobradorHoja"
                )
                .value
        );

    const fechaSeleccionada =
        document
            .getElementById(
                "fechaHojaCobranza"
            )
            .value;

    const pagosDelDia =
        pagosRecientes.filter(
            pago => {

                const cobrador =
                    obtenerCobradorDelPago(
                        pago
                    );

                const mismoCobrador =
                    !idCobrador
                    || Number(
                        cobrador.idCobrador
                        || 0
                    ) === idCobrador;

                const mismaFecha =
                    !fechaSeleccionada
                    || pago.fechaPago
                    === fechaSeleccionada;

                return mismoCobrador
                    && mismaFecha;
            }
        );

    const totalCobrado =
        pagosDelDia.reduce(
            (total, pago) =>
                total
                + Number(
                    pago.monto
                    ?? pago.montoPago
                    ?? 0
                ),
            0
        );

    document
        .getElementById(
            "totalCobradoCobranza"
        )
        .textContent =
        formatearDineroCobranza(
            totalCobrado
        );
}