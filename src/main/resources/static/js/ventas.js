let clientesVenta = [];
let salidasVenta = [];
let cobradoresVenta = [];
let productosSalidaVenta = [];
let productosSeleccionadosVenta = [];
let ventasRegistradas = [];
let ventaSeleccionada = null;

document.addEventListener(
    "DOMContentLoaded",
    async () => {

        colocarFechasVenta();
        controlarModalidadPago();

        document
            .getElementById("salidaVenta")
            .addEventListener(
                "change",
                cargarProductosSalidaSeleccionada
            );

        document
            .getElementById("productoVenta")
            .addEventListener(
                "change",
                colocarPrecioProductoVenta
            );

        document
            .getElementById(
                "btnAgregarProductoVenta"
            )
            .addEventListener(
                "click",
                agregarProductoSeleccionadoVenta
            );

        document
            .getElementById(
                "tablaProductosVenta"
            )
            .addEventListener(
                "click",
                controlarAccionProductoVenta
            );

        document
            .getElementById("cuotaInicial")
            .addEventListener(
                "input",
                actualizarResumenCreditoVenta
            );

        document
            .getElementById("modalidadPago")
            .addEventListener(
                "change",
                controlarModalidadPago
            );

        document
            .getElementById("fechaVenta")
            .addEventListener(
                "change",
                actualizarFechaPrimerPagoVenta
            );

        document
            .getElementById("formVenta")
            .addEventListener(
                "submit",
                guardarVentaCredito
            );

        document
            .getElementById("btnLimpiarVenta")
            .addEventListener(
                "click",
                limpiarFormularioVenta
            );

        document
            .getElementById("btnNuevaVenta")
            .addEventListener(
                "click",
                () => {

                    limpiarFormularioVenta();

                    document
                        .getElementById(
                            "panelNuevaVenta"
                        )
                        .scrollIntoView({
                            behavior: "smooth"
                        });
                }
            );

        document
            .getElementById("buscadorVentas")
            .addEventListener(
                "input",
                aplicarFiltrosVentas
            );

        document
            .getElementById("filtroEstadoVenta")
            .addEventListener(
                "change",
                aplicarFiltrosVentas
            );

        document
            .getElementById("tablaVentas")
            .addEventListener(
                "click",
                controlarAccionTablaVentas
            );

        document
            .getElementById(
                "btnOcultarDetalleVenta"
            )
            .addEventListener(
                "click",
                ocultarDetalleVenta
            );

        await Promise.all([
            cargarClientesVenta(),
            cargarSalidasVenta(),
            cargarCobradoresVenta(),
            cargarVentas()
        ]);
    }
);

function colocarFechasVenta() {

    const hoy = new Date();

    document
        .getElementById("fechaVenta")
        .value = convertirFechaInput(hoy);

    const primeraFechaPago =
        new Date(hoy);

    primeraFechaPago.setDate(
        primeraFechaPago.getDate() + 7
    );

    document
        .getElementById("fechaPrimerPago")
        .value =
        convertirFechaInput(
            primeraFechaPago
        );
}

function convertirFechaInput(fecha) {

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

async function cargarClientesVenta() {

    const selector =
        document.getElementById(
            "clienteVenta"
        );

    selector.innerHTML = `
        <option value="">
            Cargando clientes...
        </option>
    `;

    try {

        const respuesta =
            await fetch("/api/clientes");

        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar los clientes."
            );
        }

        const resultado =
            await respuesta.json();

        clientesVenta =
            resultado.filter(
                cliente =>
                    cliente.activo !== false
            );

        selector.innerHTML = `
            <option value="">
                Selecciona un cliente
            </option>
        `;

        for (const cliente
            of clientesVenta) {

            const opcion =
                document.createElement(
                    "option"
                );

            opcion.value =
                cliente.idCliente;

            const nombreCompleto = [
                cliente.nombres,
                cliente.apellidoPaterno,
                cliente.apellidoMaterno
            ]
                .filter(Boolean)
                .join(" ");

            const codigo =
                cliente.codigoCliente
                || cliente.codigo
                || `CLI-${String(
                    cliente.idCliente
                ).padStart(5, "0")}`;

            opcion.textContent =
                `${codigo} - ${nombreCompleto}`;

            selector.appendChild(opcion);
        }

        if (clientesVenta.length === 0) {

            selector.innerHTML = `
                <option value="">
                    No hay clientes activos
                </option>
            `;
        }

    } catch (error) {

        console.error(error);

        selector.innerHTML = `
            <option value="">
                Error al cargar clientes
            </option>
        `;

        mostrarMensajeVenta(
            error.message,
            "error"
        );
    }
}

async function cargarSalidasVenta() {

    const selector =
        document.getElementById(
            "salidaVenta"
        );

    selector.innerHTML = `
        <option value="">
            Cargando salidas...
        </option>
    `;

    try {

        const respuesta =
            await fetch("/api/salidas");

        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar las salidas."
            );
        }

        const resultado =
            await respuesta.json();

        salidasVenta =
            resultado.filter(
                salida =>
                    salida.estado === "ABIERTA"
            );

        salidasVenta.sort(
            (a, b) =>
                b.idSalida - a.idSalida
        );

        selector.innerHTML = `
            <option value="">
                Selecciona una salida abierta
            </option>
        `;

        for (const salida
            of salidasVenta) {

            const opcion =
                document.createElement(
                    "option"
                );

            opcion.value =
                salida.idSalida;

            const numeroSalida =
                `SAL-${String(
                    salida.idSalida
                ).padStart(5, "0")}`;

            opcion.textContent =
                `${numeroSalida} - `
                + `${salida.fechaSalida} - `
                + `${salida.destino}`;

            selector.appendChild(opcion);
        }

        if (salidasVenta.length === 0) {

            selector.innerHTML = `
                <option value="">
                    No hay salidas abiertas
                </option>
            `;
        }

    } catch (error) {

        console.error(error);

        selector.innerHTML = `
            <option value="">
                Error al cargar salidas
            </option>
        `;

        mostrarMensajeVenta(
            error.message,
            "error"
        );
    }
}

async function cargarCobradoresVenta() {

    const selector =
        document.getElementById(
            "cobradorVenta"
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
                "No se pudieron cargar los cobradores."
            );
        }

        cobradoresVenta =
            await respuesta.json();

        selector.innerHTML = `
            <option value="">
                Sin cobrador asignado
            </option>
        `;

        for (const cobrador
            of cobradoresVenta) {

            const opcion =
                document.createElement(
                    "option"
                );

            opcion.value =
                cobrador.idCobrador;

            const nombreCompleto = [
                cobrador.nombres,
                cobrador.apellidos
            ]
                .filter(Boolean)
                .join(" ");

            opcion.textContent =
                nombreCompleto
                || `Cobrador ${cobrador.idCobrador}`;

            selector.appendChild(opcion);
        }

    } catch (error) {

        console.error(error);

        selector.innerHTML = `
            <option value="">
                Error al cargar cobradores
            </option>
        `;

        mostrarMensajeVenta(
            error.message,
            "error"
        );
    }
}

function mostrarMensajeVenta(
    texto,
    tipo
) {

    const mensaje =
        document.getElementById(
            "mensajeVenta"
        );

    mensaje.textContent = texto;

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

async function cargarProductosSalidaSeleccionada() {

    const selectorSalida =
        document.getElementById(
            "salidaVenta"
        );

    const selectorProducto =
        document.getElementById(
            "productoVenta"
        );

    const idSalida =
        Number(selectorSalida.value);

    limpiarProductosTemporalesVenta();

    selectorProducto.innerHTML = `
        <option value="">
            Selecciona un producto
        </option>
    `;

    if (!idSalida) {

        selectorProducto.innerHTML = `
            <option value="">
                Primero selecciona una salida
            </option>
        `;

        return;
    }

    try {

        selectorProducto.innerHTML = `
            <option value="">
                Cargando productos...
            </option>
        `;

        const respuesta =
            await fetch(
                `/api/salidas/${idSalida}`
            );

        const resultado =
            await respuesta.json();

        if (!respuesta.ok) {

            throw new Error(
                resultado.mensaje
                || "No se pudo cargar la salida."
            );
        }

        productosSalidaVenta =
            (resultado.detalles || [])
                .filter(
                    detalle =>
                        detalle.stockEsperado > 0
                )
                .map(
                    detalle => ({

                        idProducto:
                            detalle.producto.idProducto,

                        codigo:
                            detalle.producto.codigo,

                        nombre:
                            detalle.producto.nombre,

                        marca:
                            detalle.producto.marca,

                        modelo:
                            detalle.producto.modelo,

                        talla:
                            detalle.producto.talla,

                        precioVenta:
                            Number(
                                detalle.producto.precioVenta
                            ),

                        stockDisponible:
                            Number(
                                detalle.stockEsperado
                            )
                    })
                );

        selectorProducto.innerHTML = `
            <option value="">
                Selecciona un producto
            </option>
        `;

        for (const producto
            of productosSalidaVenta) {

            const opcion =
                document.createElement(
                    "option"
                );

            opcion.value =
                producto.idProducto;

            opcion.textContent =
                `${producto.codigo} - `
                + `${producto.nombre} `
                + `(Disponible: `
                + `${producto.stockDisponible})`;

            selectorProducto.appendChild(
                opcion
            );
        }

        if (productosSalidaVenta.length === 0) {

            selectorProducto.innerHTML = `
                <option value="">
                    La salida no tiene productos disponibles
                </option>
            `;
        }

    } catch (error) {

        console.error(error);

        productosSalidaVenta = [];

        selectorProducto.innerHTML = `
            <option value="">
                Error al cargar productos
            </option>
        `;

        mostrarMensajeVenta(
            error.message,
            "error"
        );
    }
}

function colocarPrecioProductoVenta() {

    const idProducto =
        Number(
            document
                .getElementById(
                    "productoVenta"
                )
                .value
        );

    const campoPrecio =
        document.getElementById(
            "precioVentaCredito"
        );

    const producto =
        productosSalidaVenta.find(
            elemento =>
                elemento.idProducto
                === idProducto
        );

    if (!producto) {

        campoPrecio.value = "";
        return;
    }

    campoPrecio.value =
        producto.precioVenta.toFixed(2);
}

function limpiarProductosTemporalesVenta() {

    productosSalidaVenta = [];
    productosSeleccionadosVenta = [];

    document
        .getElementById("productoVenta")
        .innerHTML = `
            <option value="">
                Primero selecciona una salida
            </option>
        `;

    document
        .getElementById("cantidadVenta")
        .value = "1";

    document
        .getElementById("precioVentaCredito")
        .value = "";

    document
        .getElementById("tablaProductosVenta")
        .innerHTML = `
            <tr>
                <td colspan="6" class="tabla-vacia">
                    Agrega los productos vendidos.
                </td>
            </tr>
        `;

    document
        .getElementById("totalVentaVisual")
        .textContent = "S/ 0.00";

    document
        .getElementById("resumenTotal")
        .textContent = "S/ 0.00";

    document
        .getElementById("resumenInicial")
        .textContent = "S/ 0.00";

    document
        .getElementById("resumenSaldo")
        .textContent = "S/ 0.00";
}

function agregarProductoSeleccionadoVenta() {

    const idProducto =
        Number(
            document
                .getElementById(
                    "productoVenta"
                )
                .value
        );

    const cantidad =
        Number(
            document
                .getElementById(
                    "cantidadVenta"
                )
                .value
        );

    const precioUnitario =
        Number(
            document
                .getElementById(
                    "precioVentaCredito"
                )
                .value
        );

    if (!idProducto) {

        mostrarMensajeVenta(
            "Selecciona un producto.",
            "error"
        );

        return;
    }

    if (!Number.isInteger(cantidad)
        || cantidad <= 0) {

        mostrarMensajeVenta(
            "La cantidad debe ser mayor que cero.",
            "error"
        );

        return;
    }

    if (!Number.isFinite(precioUnitario)
        || precioUnitario <= 0) {

        mostrarMensajeVenta(
            "Ingresa un precio de venta válido.",
            "error"
        );

        return;
    }

    const producto =
        productosSalidaVenta.find(
            elemento =>
                elemento.idProducto
                === idProducto
        );

    if (!producto) {

        mostrarMensajeVenta(
            "No se encontró el producto seleccionado.",
            "error"
        );

        return;
    }

    const productoAgregado =
        productosSeleccionadosVenta.find(
            elemento =>
                elemento.idProducto
                === idProducto
        );

    const cantidadYaAgregada =
        productoAgregado
            ? productoAgregado.cantidad
            : 0;

    const cantidadTotal =
        cantidadYaAgregada + cantidad;

    if (cantidadTotal
        > producto.stockDisponible) {

        mostrarMensajeVenta(
            `Solo hay ${producto.stockDisponible} `
            + "unidades disponibles en la salida.",
            "error"
        );

        return;
    }

    if (productoAgregado) {

        productoAgregado.cantidad =
            cantidadTotal;

        productoAgregado.precioUnitario =
            precioUnitario;

    } else {

        productosSeleccionadosVenta.push({

            idProducto:
                producto.idProducto,

            codigo:
                producto.codigo,

            nombre:
                producto.nombre,

            marca:
                producto.marca,

            modelo:
                producto.modelo,

            talla:
                producto.talla,

            stockDisponible:
                producto.stockDisponible,

            cantidad:
                cantidad,

            precioUnitario:
                precioUnitario
        });
    }

    mostrarProductosSeleccionadosVenta();

    document
        .getElementById("productoVenta")
        .value = "";

    document
        .getElementById("cantidadVenta")
        .value = "1";

    document
        .getElementById("precioVentaCredito")
        .value = "";
}

function mostrarProductosSeleccionadosVenta() {

    const tabla =
        document.getElementById(
            "tablaProductosVenta"
        );

    if (productosSeleccionadosVenta.length
        === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="6" class="tabla-vacia">
                    Agrega los productos vendidos.
                </td>
            </tr>
        `;

        actualizarResumenCreditoVenta();
        return;
    }

    tabla.innerHTML =
        productosSeleccionadosVenta
            .map(producto => {

                const subtotal =
                    producto.cantidad
                    * producto.precioUnitario;

                const detalles = [
                    producto.codigo,
                    producto.marca,
                    producto.modelo,
                    producto.talla
                        ? `Talla ${producto.talla}`
                        : null
                ]
                    .filter(Boolean)
                    .join(" · ");

                return `
                    <tr>
                        <td>
                            <span class="producto-nombre">
                                ${escaparVenta(
                    producto.nombre
                )}
                            </span>

                            <span class="producto-secundario">
                                ${escaparVenta(
                    detalles
                )}
                            </span>
                        </td>

                        <td>
                            ${producto.stockDisponible}
                        </td>

                        <td>
                            <strong>
                                ${producto.cantidad}
                            </strong>
                        </td>

                        <td>
                            ${formatearDineroVenta(
                    producto.precioUnitario
                )}
                        </td>

                        <td>
                            <strong>
                                ${formatearDineroVenta(
                    subtotal
                )}
                            </strong>
                        </td>

                        <td>
                            <button
                                type="button"
                                class="boton-tabla"
                                data-accion="quitar-producto"
                                data-id="${producto.idProducto}"
                            >
                                Quitar
                            </button>
                        </td>
                    </tr>
                `;
            })
            .join("");

    actualizarResumenCreditoVenta();
}

function controlarAccionProductoVenta(
    evento
) {

    const boton =
        evento.target.closest(
            "button[data-accion='quitar-producto']"
        );

    if (!boton) {
        return;
    }

    const idProducto =
        Number(boton.dataset.id);

    productosSeleccionadosVenta =
        productosSeleccionadosVenta.filter(
            producto =>
                producto.idProducto
                !== idProducto
        );

    mostrarProductosSeleccionadosVenta();
}

function calcularTotalVenta() {

    return productosSeleccionadosVenta.reduce(
        (total, producto) =>
            total
            + producto.cantidad
            * producto.precioUnitario,
        0
    );
}

function actualizarResumenCreditoVenta() {

    const total =
        calcularTotalVenta();

    const cuotaInicialIngresada =
        Number(
            document
                .getElementById(
                    "cuotaInicial"
                )
                .value
        );

    const cuotaInicial =
        Number.isFinite(
            cuotaInicialIngresada
        )
            ? cuotaInicialIngresada
            : 0;

    const saldo =
        Math.max(
            total - cuotaInicial,
            0
        );

    document
        .getElementById(
            "totalVentaVisual"
        )
        .textContent =
        formatearDineroVenta(total);

    document
        .getElementById("resumenTotal")
        .textContent =
        formatearDineroVenta(total);

    document
        .getElementById("resumenInicial")
        .textContent =
        formatearDineroVenta(
            cuotaInicial
        );

    document
        .getElementById("resumenSaldo")
        .textContent =
        formatearDineroVenta(saldo);
}

function formatearDineroVenta(
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

function escaparVenta(valor) {

    const elemento =
        document.createElement("div");

    elemento.textContent =
        valor ?? "";

    return elemento.innerHTML;
}

function controlarModalidadPago() {

    const modalidad =
        document
            .getElementById(
                "modalidadPago"
            )
            .value;

    const campoDiaPago =
        document.getElementById(
            "campoDiaPago"
        );

    const selectorDia =
        document.getElementById(
            "diaPago"
        );

    const esSemanal =
        modalidad === "SEMANAL";

    campoDiaPago.classList.toggle(
        "oculto",
        !esSemanal
    );

    selectorDia.disabled =
        !esSemanal;

    if (!esSemanal) {
        selectorDia.value = "";
    }

    if (esSemanal
        && !selectorDia.value) {

        selectorDia.value =
            "SABADO";
    }

    actualizarFechaPrimerPagoVenta();
}

function actualizarFechaPrimerPagoVenta() {

    const fechaVentaTexto =
        document
            .getElementById(
                "fechaVenta"
            )
            .value;

    const modalidad =
        document
            .getElementById(
                "modalidadPago"
            )
            .value;

    if (!fechaVentaTexto) {
        return;
    }

    const partes =
        fechaVentaTexto.split("-");

    const fechaPrimerPago =
        new Date(
            Number(partes[0]),
            Number(partes[1]) - 1,
            Number(partes[2])
        );

    let diasAgregar = 7;

    switch (modalidad) {

        case "DIARIO":
            diasAgregar = 1;
            break;

        case "SEMANAL":
            diasAgregar = 7;
            break;

        case "QUINCENAL":
            diasAgregar = 15;
            break;

        case "MENSUAL":
            diasAgregar = 30;
            break;

        default:
            diasAgregar = 7;
    }

    fechaPrimerPago.setDate(
        fechaPrimerPago.getDate()
        + diasAgregar
    );

    document
        .getElementById(
            "fechaPrimerPago"
        )
        .value =
        convertirFechaInput(
            fechaPrimerPago
        );
}

async function guardarVentaCredito(
    evento
) {

    evento.preventDefault();

    const idCliente =
        Number(
            document
                .getElementById(
                    "clienteVenta"
                )
                .value
        );

    const idSalida =
        Number(
            document
                .getElementById(
                    "salidaVenta"
                )
                .value
        );

    const idCobrador =
        Number(
            document
                .getElementById(
                    "cobradorVenta"
                )
                .value
        );

    const fechaVenta =
        document
            .getElementById(
                "fechaVenta"
            )
            .value;

    const cuotaInicial =
        Number(
            document
                .getElementById(
                    "cuotaInicial"
                )
                .value
        ) || 0;

    const modalidadPago =
        document
            .getElementById(
                "modalidadPago"
            )
            .value;

    const montoCuota =
        Number(
            document
                .getElementById(
                    "montoCuota"
                )
                .value
        );

    const diaPago =
        document
            .getElementById(
                "diaPago"
            )
            .value;

    const fechaPrimerPago =
        document
            .getElementById(
                "fechaPrimerPago"
            )
            .value;

    const observaciones =
        document
            .getElementById(
                "observacionesVenta"
            )
            .value
            .trim();

    const total =
        calcularTotalVenta();

    if (!idCliente) {

        mostrarMensajeVenta(
            "Selecciona un cliente.",
            "error"
        );

        return;
    }

    if (!idSalida) {

        mostrarMensajeVenta(
            "Selecciona una salida de mercadería.",
            "error"
        );

        return;
    }

    if (!fechaVenta) {

        mostrarMensajeVenta(
            "Selecciona la fecha de venta.",
            "error"
        );

        return;
    }

    if (productosSeleccionadosVenta.length
        === 0) {

        mostrarMensajeVenta(
            "Agrega al menos un producto.",
            "error"
        );

        return;
    }

    if (!Number.isFinite(cuotaInicial)
        || cuotaInicial < 0) {

        mostrarMensajeVenta(
            "La cuota inicial no es válida.",
            "error"
        );

        return;
    }

    if (cuotaInicial >= total) {

        mostrarMensajeVenta(
            "La cuota inicial debe ser menor "
            + "que el total de la venta.",
            "error"
        );

        return;
    }

    if (!modalidadPago) {

        mostrarMensajeVenta(
            "Selecciona una modalidad de pago.",
            "error"
        );

        return;
    }

    if (!Number.isFinite(montoCuota)
        || montoCuota <= 0) {

        mostrarMensajeVenta(
            "El monto de la cuota debe ser "
            + "mayor que cero.",
            "error"
        );

        return;
    }

    if (montoCuota > total - cuotaInicial) {

        mostrarMensajeVenta(
            "El monto de la cuota no puede "
            + "ser mayor que el saldo.",
            "error"
        );

        return;
    }

    if (modalidadPago === "SEMANAL"
        && !diaPago) {

        mostrarMensajeVenta(
            "Selecciona el día de pago semanal.",
            "error"
        );

        return;
    }

    if (!fechaPrimerPago) {

        mostrarMensajeVenta(
            "Selecciona la primera fecha de pago.",
            "error"
        );

        return;
    }

    const solicitud = {

        idCliente:
            idCliente,

        idSalida:
            idSalida,

        fechaVenta:
            fechaVenta,

        cuotaInicial:
            cuotaInicial,

        modalidadPago:
            modalidadPago,

        montoCuota:
            montoCuota,

        diaPago:
            modalidadPago === "SEMANAL"
                ? diaPago
                : null,

        fechaPrimerPago:
            fechaPrimerPago,

        observaciones:
            observaciones || null,

        detalles:
            productosSeleccionadosVenta.map(
                producto => ({

                    idProducto:
                        producto.idProducto,

                    cantidad:
                        producto.cantidad,

                    precioUnitario:
                        producto.precioUnitario
                })
            )
    };

    const boton =
        document.getElementById(
            "btnGuardarVenta"
        );

    const textoOriginal =
        boton.textContent;

    try {

        boton.disabled = true;

        boton.textContent =
            "Guardando contrato...";

        const respuesta =
            await fetch(
                "/api/ventas",
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

        const resultado =
            await respuesta.json();

        if (!respuesta.ok) {

            throw new Error(
                resultado.mensaje
                || "No se pudo registrar la venta."
            );
        }

        let cobradorAsignado = true;

        if (idCobrador) {

            cobradorAsignado =
                await asignarCobradorVenta(
                    resultado.idVenta,
                    idCobrador
                );
        }

        const numeroContrato =
            resultado.numeroContrato
            || `CTR-${String(
                resultado.idVenta
            ).padStart(6, "0")}`;

        const textoCobrador =
            cobradorAsignado
                ? ""
                : " La venta se guardó, pero "
                + "no se pudo asignar el cobrador.";

        mostrarMensajeVenta(
            `Contrato ${numeroContrato} `
            + "registrado correctamente."
            + textoCobrador,
            "exito"
        );

        limpiarFormularioVenta();

        await cargarSalidasVenta();
        await cargarVentas();

    } catch (error) {

        console.error(error);

        mostrarMensajeVenta(
            error.message,
            "error"
        );

    } finally {

        boton.disabled = false;
        boton.textContent =
            textoOriginal;
    }
}

async function asignarCobradorVenta(
    idVenta,
    idCobrador
) {

    try {

        const respuesta =
            await fetch(
                `/api/ventas/${idVenta}/cobrador`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify({
                            idCobrador:
                                idCobrador
                        })
                }
            );

        if (!respuesta.ok) {

            const resultado =
                await respuesta.json();

            throw new Error(
                resultado.mensaje
                || "No se pudo asignar el cobrador."
            );
        }

        return true;

    } catch (error) {

        console.error(error);
        return false;
    }
}

function limpiarFormularioVenta() {

    document
        .getElementById("formVenta")
        .reset();

    productosSalidaVenta = [];
    productosSeleccionadosVenta = [];

    limpiarProductosTemporalesVenta();

    colocarFechasVenta();
    controlarModalidadPago();

    document
        .getElementById("clienteVenta")
        .value = "";

    document
        .getElementById("salidaVenta")
        .value = "";

    document
        .getElementById("cobradorVenta")
        .value = "";
}

async function cargarVentas() {

    const tabla =
        document.getElementById(
            "tablaVentas"
        );

    tabla.innerHTML = `
        <tr>
            <td colspan="7" class="tabla-vacia">
                Cargando contratos...
            </td>
        </tr>
    `;

    try {

        const respuesta =
            await fetch("/api/ventas");

        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar los contratos."
            );
        }

        ventasRegistradas =
            await respuesta.json();

        ventasRegistradas.sort(
            (a, b) =>
                b.idVenta - a.idVenta
        );

        aplicarFiltrosVentas();

    } catch (error) {

        console.error(error);

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    No se pudieron cargar los contratos.
                </td>
            </tr>
        `;

        mostrarMensajeVenta(
            error.message,
            "error"
        );
    }
}

function aplicarFiltrosVentas() {

    const texto =
        document
            .getElementById(
                "buscadorVentas"
            )
            .value
            .trim()
            .toLowerCase();

    const estadoSeleccionado =
        document
            .getElementById(
                "filtroEstadoVenta"
            )
            .value;

    const ventasFiltradas =
        ventasRegistradas.filter(
            venta => {

                const nombreCliente =
                    obtenerNombreClienteVenta(
                        venta
                    );

                const numeroContrato =
                    obtenerNumeroContrato(
                        venta
                    );

                const contenido = [
                    numeroContrato,
                    venta.fechaVenta,
                    nombreCliente,
                    venta.estado
                ]
                    .filter(Boolean)
                    .join(" ")
                    .toLowerCase();

                const coincideTexto =
                    contenido.includes(texto);

                const mostrarTodos =
                    !estadoSeleccionado
                    || estadoSeleccionado === "TODOS"
                    || estadoSeleccionado === "TODAS";

                const coincideEstado =
                    mostrarTodos
                    || venta.estado
                        === estadoSeleccionado;

                return coincideTexto
                    && coincideEstado;
            }
        );

    mostrarVentas(
        ventasFiltradas
    );
}

function mostrarVentas(ventas) {

    const tabla =
        document.getElementById(
            "tablaVentas"
        );

    document
        .getElementById(
            "contadorVentas"
        )
        .textContent =
        ventas.length;

    if (ventas.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    No se encontraron contratos.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML =
        ventas.map(venta => {

            const numeroContrato =
                obtenerNumeroContrato(
                    venta
                );

            const nombreCliente =
                obtenerNombreClienteVenta(
                    venta
                );

            const total =
                Number(
                    venta.totalVenta
                    ?? venta.total
                    ?? 0
                );

            const saldo =
                Number(
                    venta.saldoPendiente
                    ?? venta.saldo
                    ?? 0
                );

            const estado =
                venta.estado
                || "PENDIENTE";

            const claseEstado =
                `estado-venta-${estado
                    .toLowerCase()}`;

            return `
                <tr>
                    <td>
                        <strong>
                            ${escaparVenta(
                                numeroContrato
                            )}
                        </strong>
                    </td>

                    <td>
                        ${formatearFechaVenta(
                            venta.fechaVenta
                        )}
                    </td>

                    <td>
                        <span class="producto-nombre">
                            ${escaparVenta(
                                nombreCliente
                            )}
                        </span>
                    </td>

                    <td>
                        <strong>
                            ${formatearDineroVenta(
                                total
                            )}
                        </strong>
                    </td>

                    <td>
                        <strong>
                            ${formatearDineroVenta(
                                saldo
                            )}
                        </strong>
                    </td>

                    <td>
                        <span
                            class="insignia-estado ${claseEstado}"
                        >
                            ${escaparVenta(
                                estado
                            )}
                        </span>
                    </td>

                    <td>
                        <button
                            type="button"
                            class="boton-tabla boton-detalle-venta"
                            data-accion="ver-venta"
                            data-id="${venta.idVenta}"
                        >
                            Ver detalle
                        </button>
                    </td>
                </tr>
            `;
        }).join("");
}

function obtenerNumeroContrato(
    venta
) {

    if (venta.numeroContrato) {
        return venta.numeroContrato;
    }

    return `CTR-${String(
        venta.idVenta
    ).padStart(6, "0")}`;
}

function obtenerNombreClienteVenta(
    venta
) {

    const cliente =
        venta.cliente || {};

    if (cliente.nombreCompleto) {
        return cliente.nombreCompleto;
    }

    const nombreCompleto = [
        cliente.nombres,
        cliente.apellidoPaterno,
        cliente.apellidoMaterno
    ]
        .filter(Boolean)
        .join(" ");

    if (nombreCompleto) {
        return nombreCompleto;
    }

    return venta.nombreCliente
        || "Cliente no disponible";
}

function formatearFechaVenta(
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

function controlarAccionTablaVentas(
    evento
) {

    const boton =
        evento.target.closest(
            "button[data-accion='ver-venta']"
        );

    if (!boton) {
        return;
    }

    const idVenta =
        Number(boton.dataset.id);

    abrirDetalleVenta(
        idVenta
    );
}

async function abrirDetalleVenta(
    idVenta
) {

    const panel =
        document.getElementById(
            "panelDetalleVenta"
        );

    const datos =
        document.getElementById(
            "datosDetalleVenta"
        );

    const tablaCuotas =
        document.getElementById(
            "tablaCuotasVenta"
        );

    panel.classList.remove(
        "oculto"
    );

    datos.innerHTML = `
        <p>Cargando contrato...</p>
    `;

    tablaCuotas.innerHTML = `
        <tr>
            <td colspan="6" class="tabla-vacia">
                Cargando cuotas...
            </td>
        </tr>
    `;

    try {

        const respuestas =
            await Promise.all([
                fetch(
                    `/api/ventas/${idVenta}`
                ),
                fetch(
                    `/api/ventas/${idVenta}/cuotas`
                )
            ]);

        const respuestaVenta =
            respuestas[0];

        const respuestaCuotas =
            respuestas[1];

        const venta =
            await respuestaVenta.json();

        const cuotas =
            await respuestaCuotas.json();

        if (!respuestaVenta.ok) {

            throw new Error(
                venta.mensaje
                || "No se pudo cargar el contrato."
            );
        }

        if (!respuestaCuotas.ok) {

            throw new Error(
                cuotas.mensaje
                || "No se pudieron cargar las cuotas."
            );
        }

        ventaSeleccionada =
            venta;

        mostrarDetalleContrato(
            venta
        );

        mostrarCuotasContrato(
            cuotas
        );

        panel.scrollIntoView({
            behavior: "smooth"
        });

    } catch (error) {

        console.error(error);

        datos.innerHTML = `
            <p class="mensaje-error-producto">
                ${escaparVenta(
                    error.message
                )}
            </p>
        `;

        tablaCuotas.innerHTML = `
            <tr>
                <td colspan="6" class="tabla-vacia">
                    No se pudo cargar el cronograma.
                </td>
            </tr>
        `;

        mostrarMensajeVenta(
            error.message,
            "error"
        );
    }
}

function mostrarDetalleContrato(
    venta
) {

    const numeroContrato =
        obtenerNumeroContrato(
            venta
        );

    const cliente =
        obtenerNombreClienteVenta(
            venta
        );

    const total =
        Number(
            venta.totalVenta
            ?? venta.total
            ?? 0
        );

    const cuotaInicial =
        Number(
            venta.cuotaInicial
            ?? 0
        );

    const saldo =
        Number(
            venta.saldoPendiente
            ?? venta.saldo
            ?? 0
        );

    const modalidad =
        venta.modalidadPago
        || "Sin modalidad";

    const diaPago =
        venta.diaPago
            ? ` - ${venta.diaPago}`
            : "";

    const cobrador =
        obtenerNombreCobradorVenta(
            venta
        );

    document
        .getElementById(
            "tituloDetalleVenta"
        )
        .textContent =
        `Contrato ${numeroContrato}`;

    document
        .getElementById(
            "informacionDetalleVenta"
        )
        .textContent =
        `${formatearFechaVenta(
            venta.fechaVenta
        )} · ${cliente} · `
        + `${venta.estado || "PENDIENTE"}`;

    document
        .getElementById(
            "datosDetalleVenta"
        )
        .innerHTML = `
            <div class="detalle-contrato-grid">

                <div>
                    <span>Cliente</span>
                    <strong>
                        ${escaparVenta(cliente)}
                    </strong>
                </div>

                <div>
                    <span>Modalidad</span>
                    <strong>
                        ${escaparVenta(
                            modalidad + diaPago
                        )}
                    </strong>
                </div>

                <div>
                    <span>Total de venta</span>
                    <strong>
                        ${formatearDineroVenta(
                            total
                        )}
                    </strong>
                </div>

                <div>
                    <span>Cuota inicial</span>
                    <strong>
                        ${formatearDineroVenta(
                            cuotaInicial
                        )}
                    </strong>
                </div>

                <div>
                    <span>Saldo pendiente</span>
                    <strong>
                        ${formatearDineroVenta(
                            saldo
                        )}
                    </strong>
                </div>

                <div>
                    <span>Monto de cuota</span>
                    <strong>
                        ${formatearDineroVenta(
                            venta.montoCuota
                            ?? 0
                        )}
                    </strong>
                </div>

                <div>
                    <span>Primera fecha de pago</span>
                    <strong>
                        ${formatearFechaVenta(
                            venta.fechaPrimerPago
                        )}
                    </strong>
                </div>

                <div>
                    <span>Cobrador</span>
                    <strong>
                        ${escaparVenta(cobrador)}
                    </strong>
                </div>

            </div>

            <div class="observacion-contrato">
                <strong>Observaciones:</strong>
                ${escaparVenta(
                    venta.observaciones
                    || "Sin observaciones"
                )}
            </div>
        `;
}

function obtenerNombreCobradorVenta(
    venta
) {

    const cobrador =
        venta.cobrador || {};

    const nombreCompleto = [
        cobrador.nombres,
        cobrador.apellidos
    ]
        .filter(Boolean)
        .join(" ");

    if (nombreCompleto) {
        return nombreCompleto;
    }

    return venta.nombreCobrador
        || "Sin cobrador asignado";
}

function mostrarCuotasContrato(
    cuotas
) {

    const tabla =
        document.getElementById(
            "tablaCuotasVenta"
        );

    if (!Array.isArray(cuotas)
        || cuotas.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="6" class="tabla-vacia">
                    El contrato no tiene cuotas.
                </td>
            </tr>
        `;

        return;
    }

    cuotas.sort(
        (a, b) =>
            (a.numeroCuota || 0)
            - (b.numeroCuota || 0)
    );

    tabla.innerHTML =
        cuotas.map(cuota => {

            const monto =
                Number(
                    cuota.montoProgramado
                    ?? cuota.montoCuota
                    ?? cuota.monto
                    ?? 0
                );

            const pagado =
                Number(
                    cuota.montoPagado
                    ?? 0
                );

            const saldo =
                Number(
                    cuota.saldoCuota
                    ?? cuota.saldo
                    ?? monto - pagado
                );

            const estado =
                cuota.estado
                || "PENDIENTE";

            const claseEstado =
                `estado-cuota-${estado
                    .toLowerCase()}`;

            return `
                <tr>
                    <td>
                        <strong>
                            ${cuota.numeroCuota
                            ?? "-"}
                        </strong>
                    </td>

                    <td>
                        ${formatearFechaVenta(
                            cuota.fechaVencimiento
                        )}
                    </td>

                    <td>
                        ${formatearDineroVenta(
                            monto
                        )}
                    </td>

                    <td>
                        ${formatearDineroVenta(
                            pagado
                        )}
                    </td>

                    <td>
                        <strong>
                            ${formatearDineroVenta(
                                saldo
                            )}
                        </strong>
                    </td>

                    <td>
                        <span
                            class="insignia-estado ${claseEstado}"
                        >
                            ${escaparVenta(
                                estado
                            )}
                        </span>
                    </td>
                </tr>
            `;
        }).join("");
}

function ocultarDetalleVenta() {

    document
        .getElementById(
            "panelDetalleVenta"
        )
        .classList.add(
            "oculto"
        );

    ventaSeleccionada = null;
}