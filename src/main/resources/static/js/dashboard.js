let productosDashboard = [];
let salidasDashboard = [];
let ventasDashboard = [];
let pagosDashboard = [];

document.addEventListener(
    "DOMContentLoaded",
    async () => {

        colocarFechaDashboard();

        document
            .getElementById(
                "btnActualizarDashboard"
            )
            .addEventListener(
                "click",
                cargarDashboard
            );

        await cargarDashboard();
    }
);

function colocarFechaDashboard() {

    const hoy =
        new Date();

    document
        .getElementById(
            "fechaDashboard"
        )
        .textContent =
        new Intl.DateTimeFormat(
            "es-PE",
            {
                dateStyle: "full"
            }
        ).format(hoy);
}

async function cargarDashboard() {

    const boton =
        document.getElementById(
            "btnActualizarDashboard"
        );

    const textoOriginal =
        boton.textContent;

    try {

        boton.disabled = true;
        boton.textContent =
            "Actualizando...";

        const respuestas =
            await Promise.all([
                obtenerDatosDashboard(
                    "/api/productos/activos"
                ),
                obtenerDatosDashboard(
                    "/api/salidas"
                ),
                obtenerDatosDashboard(
                    "/api/ventas"
                ),
                obtenerDatosDashboard(
                    "/api/pagos"
                )
            ]);

        productosDashboard =
            respuestas[0];

        salidasDashboard =
            respuestas[1];

        ventasDashboard =
            respuestas[2];

        pagosDashboard =
            respuestas[3];

        actualizarTarjetasDashboard();

        mostrarStockBajoDashboard();

                await cargarCuotasVencidasDashboard();

        mostrarMensajeDashboard(
            "Dashboard actualizado correctamente.",
            "exito"
        );

    } catch (error) {

        console.error(error);

        mostrarMensajeDashboard(
            error.message,
            "error"
        );

    } finally {

        boton.disabled = false;
        boton.textContent =
            textoOriginal;
    }
}

async function obtenerDatosDashboard(
    direccion
) {

    const respuesta =
        await fetch(direccion);

    if (!respuesta.ok) {

        throw new Error(
            `No se pudo consultar ${direccion}`
        );
    }

    const resultado =
        await respuesta.json();

    return Array.isArray(resultado)
        ? resultado
        : [];
}

function actualizarTarjetasDashboard() {

    const stockTotal =
        productosDashboard.reduce(
            (total, producto) =>
                total
                + Number(
                    producto.stockAlmacen
                    ?? 0
                ),
            0
        );

    const salidasAbiertas =
        salidasDashboard.filter(
            salida =>
                salida.estado === "ABIERTA"
        );

    const ventasActivas =
        ventasDashboard.filter(
            venta =>
                esVentaActivaDashboard(
                    venta
                )
        );

    const deudaPendiente =
        ventasActivas.reduce(
            (total, venta) =>
                total
                + obtenerSaldoVentaDashboard(
                    venta
                ),
            0
        );

    const fechaActual =
        convertirFechaDashboardInput(
            new Date()
        );

    const cobradoHoy =
        pagosDashboard
            .filter(
                pago =>
                    pago.fechaPago
                    === fechaActual
            )
            .reduce(
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
            "dashboardTotalProductos"
        )
        .textContent =
        productosDashboard.length;

    document
        .getElementById(
            "dashboardStockAlmacen"
        )
        .textContent =
        stockTotal;

    document
        .getElementById(
            "dashboardSalidasAbiertas"
        )
        .textContent =
        salidasAbiertas.length;

    document
        .getElementById(
            "dashboardVentasActivas"
        )
        .textContent =
        ventasActivas.length;

    document
        .getElementById(
            "dashboardDeudaPendiente"
        )
        .textContent =
        formatearDineroDashboard(
            deudaPendiente
        );

    document
        .getElementById(
            "dashboardCobradoHoy"
        )
        .textContent =
        formatearDineroDashboard(
            cobradoHoy
        );
}

function esVentaActivaDashboard(
    venta
) {

    const estado =
        venta.estado || "";

    const saldo =
        obtenerSaldoVentaDashboard(
            venta
        );

    const terminada =
        estado === "PAGADA"
        || estado === "ANULADA"
        || estado === "CANCELADA";

    return !terminada
        && saldo > 0;
}

function obtenerSaldoVentaDashboard(
    venta
) {

    return Number(
        venta.saldoPendiente
        ?? venta.saldo
        ?? 0
    );
}

function mostrarStockBajoDashboard() {

    const tabla =
        document.getElementById(
            "tablaStockBajoDashboard"
        );

    const productosStockBajo =
        productosDashboard
            .filter(producto => {

                const stock =
                    Number(
                        producto.stockAlmacen
                        ?? 0
                    );

                const minimo =
                    Number(
                        producto.stockMinimo
                        ?? 0
                    );

                return stock <= minimo;
            })
            .sort(
                (a, b) =>
                    Number(
                        a.stockAlmacen
                        ?? 0
                    )
                    - Number(
                        b.stockAlmacen
                        ?? 0
                    )
            );

    if (productosStockBajo.length
        === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="4" class="tabla-vacia">
                    No hay productos con stock bajo.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML =
        productosStockBajo
            .map(producto => {

                const stock =
                    Number(
                        producto.stockAlmacen
                        ?? 0
                    );

                const minimo =
                    Number(
                        producto.stockMinimo
                        ?? 0
                    );

                return `
                    <tr>
                        <td>
                            <strong>
                                ${escaparDashboard(
                                    producto.codigo
                                )}
                            </strong>
                        </td>

                        <td>
                            ${escaparDashboard(
                                producto.nombre
                            )}
                        </td>

                        <td>
                            <span class="stock-dashboard-bajo">
                                ${stock}
                            </span>
                        </td>

                        <td>
                            ${minimo}
                        </td>
                    </tr>
                `;
            })
            .join("");
}

function convertirFechaDashboardInput(
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

function formatearDineroDashboard(
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

function escaparDashboard(
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

function mostrarMensajeDashboard(
    texto,
    tipo
) {

    const mensaje =
        document.getElementById(
            "mensajeDashboard"
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
        4000
    );
}

async function cargarCuotasVencidasDashboard() {

    const tabla =
        document.getElementById(
            "tablaCuotasVencidasDashboard"
        );

    tabla.innerHTML = `
        <tr>
            <td colspan="5" class="tabla-vacia">
                Cargando cuotas vencidas...
            </td>
        </tr>
    `;

    const ventasActivas =
        ventasDashboard.filter(
            venta =>
                esVentaActivaDashboard(
                    venta
                )
        );

    const fechaActual =
        convertirFechaDashboardInput(
            new Date()
        );

    const resultados =
        await Promise.all(
            ventasActivas.map(
                venta =>
                    obtenerCuotasVencidasVenta(
                        venta,
                        fechaActual
                    )
            )
        );

    const cuotasVencidas =
        resultados
            .flat()
            .sort(
                (a, b) =>
                    (
                        a.cuota.fechaVencimiento
                        || ""
                    ).localeCompare(
                        b.cuota.fechaVencimiento
                        || ""
                    )
            );

    mostrarCuotasVencidasDashboard(
        cuotasVencidas
    );
}

async function obtenerCuotasVencidasVenta(
    venta,
    fechaActual
) {

    try {

        const respuesta =
            await fetch(
                `/api/ventas/${venta.idVenta}/cuotas`
            );

        if (!respuesta.ok) {

            console.error(
                "No se cargaron las cuotas "
                + `de la venta ${venta.idVenta}`
            );

            return [];
        }

        const cuotas =
            await respuesta.json();

        return cuotas
            .filter(cuota => {

                const estado =
                    cuota.estado || "";

                const fechaVencimiento =
                    cuota.fechaVencimiento;

                const pendiente =
                    estado !== "PAGADA"
                    && estado !== "ANULADA";

                const vencida =
                    fechaVencimiento
                    && fechaVencimiento
                        < fechaActual;

                return pendiente
                    && vencida;
            })
            .map(cuota => ({
                venta:
                    venta,

                cuota:
                    cuota
            }));

    } catch (error) {

        console.error(error);
        return [];
    }
}

function mostrarCuotasVencidasDashboard(
    registros
) {

    const tabla =
        document.getElementById(
            "tablaCuotasVencidasDashboard"
        );

    if (registros.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="5" class="tabla-vacia">
                    No hay cuotas vencidas.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML =
        registros
            .map(registro => {

                const venta =
                    registro.venta;

                const cuota =
                    registro.cuota;

                const saldoCuota =
                    Number(
                        cuota.saldoCuota
                        ?? cuota.saldo
                        ?? cuota.montoProgramado
                        ?? cuota.montoCuota
                        ?? cuota.monto
                        ?? 0
                    );

                return `
                    <tr>
                        <td>
                            <strong>
                                ${escaparDashboard(
                                    obtenerNumeroContratoDashboard(
                                        venta
                                    )
                                )}
                            </strong>
                        </td>

                        <td>
                            ${escaparDashboard(
                                obtenerNombreClienteDashboard(
                                    venta
                                )
                            )}
                        </td>

                        <td>
                            <span class="cuota-dashboard-vencida">
                                ${formatearFechaDashboard(
                                    cuota.fechaVencimiento
                                )}
                            </span>
                        </td>

                        <td>
                            <strong>
                                ${formatearDineroDashboard(
                                    saldoCuota
                                )}
                            </strong>
                        </td>

                        <td>
                            <span class="estado-dashboard-vencida">
                                VENCIDA
                            </span>
                        </td>
                    </tr>
                `;
            })
            .join("");
}

function obtenerNumeroContratoDashboard(
    venta
) {

    return venta.numeroContrato
        || `CTR-${String(
            venta.idVenta
        ).padStart(6, "0")}`;
}

function obtenerNombreClienteDashboard(
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

    return nombreCompleto
        || venta.nombreCliente
        || "Cliente no disponible";
}

function formatearFechaDashboard(
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