let productosDisponibles = [];
let productosCarga = [];
let salidasRegistradas = [];
let salidaSeleccionada = null;

document.addEventListener(
    "DOMContentLoaded",
    () => {

        colocarFechaActual();

        document
            .getElementById("btnAgregarProductoSalida")
            .addEventListener(
                "click",
                agregarProductoCarga
            );

        document
            .getElementById("tablaCarga")
            .addEventListener(
                "click",
                controlarAccionCarga
            );

        document
            .getElementById("btnLimpiarSalida")
            .addEventListener(
                "click",
                limpiarFormularioSalida
            );

        document
            .getElementById("btnNuevaSalida")
            .addEventListener(
                "click",
                () => {

                    limpiarFormularioSalida();

                    document
                        .getElementById("panelNuevaSalida")
                        .scrollIntoView({
                            behavior: "smooth"
                        });
                }
            );

        document
            .getElementById("formSalida")
            .addEventListener(
                "submit",
                guardarSalida
            );

        document
            .getElementById("buscadorSalidas")
            .addEventListener(
                "input",
                aplicarFiltrosSalidas
            );

        document
            .getElementById("filtroEstadoSalida")
            .addEventListener(
                "change",
                aplicarFiltrosSalidas
            );

        document
            .getElementById("tablaSalidas")
            .addEventListener(
                "click",
                controlarAccionSalida
            );

        document
            .getElementById("btnCerrarPanelKardex")
            .addEventListener(
                "click",
                () => {

                    document
                        .getElementById("panelKardex")
                        .classList.add("oculto");

                    salidaSeleccionada = null;
                }
            );

        document
            .getElementById("btnCerrarSalida")
            .addEventListener(
                "click",
                cerrarSalidaSeleccionada
            );

        document
            .getElementById("tablaKardex")
            .addEventListener(
                "input",
                actualizarDiferenciaVisual
            );

        cargarProductosDisponibles();
        cargarSalidas();
    }
);

function colocarFechaActual() {

    const hoy =
        new Date();

    const anio =
        hoy.getFullYear();

    const mes =
        String(
            hoy.getMonth() + 1
        ).padStart(2, "0");

    const dia =
        String(
            hoy.getDate()
        ).padStart(2, "0");

    document
        .getElementById("fechaSalida")
        .value =
        `${anio}-${mes}-${dia}`;
}

async function cargarProductosDisponibles() {

    const selector =
        document.getElementById(
            "productoSalida"
        );

    try {
        const respuesta =
            await fetch(
                "/api/productos/activos"
            );

        if (!respuesta.ok) {
            throw new Error(
                "No se pudieron cargar los productos"
            );
        }

        productosDisponibles =
            await respuesta.json();

        mostrarOpcionesProductos();

    } catch (error) {

        console.error(error);

        selector.innerHTML = `
            <option value="">
                Error al cargar productos
            </option>
        `;

        mostrarMensajeSalida(
            error.message,
            "error"
        );
    }
}

function mostrarOpcionesProductos() {

    const selector =
        document.getElementById(
            "productoSalida"
        );

    const productosConStock =
        productosDisponibles.filter(
            producto =>
                producto.stockAlmacen > 0
        );

    selector.innerHTML = `
        <option value="">
            Selecciona un producto
        </option>
    `;

    for (const producto
        of productosConStock) {

        const opcion =
            document.createElement("option");

        opcion.value =
            producto.idProducto;

        opcion.textContent =
            `${producto.codigo} - `
            + `${producto.nombre} `
            + `(Stock: ${producto.stockAlmacen})`;

        selector.appendChild(opcion);
    }
}

function agregarProductoCarga() {

    const idProducto =
        Number(
            document
                .getElementById(
                    "productoSalida"
                )
                .value
        );

    const cantidad =
        Number(
            document
                .getElementById(
                    "cantidadSalida"
                )
                .value
        );

    if (!idProducto) {

        mostrarMensajeSalida(
            "Selecciona un producto.",
            "error"
        );

        return;
    }

    if (!Number.isInteger(cantidad)
        || cantidad <= 0) {

        mostrarMensajeSalida(
            "La cantidad debe ser mayor que cero.",
            "error"
        );

        return;
    }

    const producto =
        productosDisponibles.find(
            elemento =>
                elemento.idProducto
                === idProducto
        );

    if (!producto) {

        mostrarMensajeSalida(
            "No se encontró el producto.",
            "error"
        );

        return;
    }

    if (cantidad > producto.stockAlmacen) {

        mostrarMensajeSalida(
            `Stock insuficiente. Solo hay `
            + `${producto.stockAlmacen} unidades.`,
            "error"
        );

        return;
    }

    const productoYaAgregado =
        productosCarga.find(
            elemento =>
                elemento.idProducto
                === idProducto
        );

    if (productoYaAgregado) {

        const nuevaCantidad =
            productoYaAgregado.cantidad
            + cantidad;

        if (nuevaCantidad
            > producto.stockAlmacen) {

            mostrarMensajeSalida(
                "La cantidad total supera "
                + "el stock del almacén.",
                "error"
            );

            return;
        }

        productoYaAgregado.cantidad =
            nuevaCantidad;

    } else {

        productosCarga.push({
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

            stockAlmacen:
                producto.stockAlmacen,

            cantidad:
                cantidad
        });
    }

    mostrarCarga();

    document
        .getElementById("productoSalida")
        .value = "";

    document
        .getElementById("cantidadSalida")
        .value = "1";
}

function mostrarCarga() {

    const tabla =
        document.getElementById(
            "tablaCarga"
        );

    if (productosCarga.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="5" class="tabla-vacia">
                    Agrega productos a la salida.
                </td>
            </tr>
        `;

        document
            .getElementById(
                "totalUnidadesCarga"
            )
            .textContent = "0";

        return;
    }

    tabla.innerHTML =
        productosCarga.map(producto => {

            const detalles = [
                producto.marca,
                producto.modelo,
                producto.talla
                    ? "Talla " + producto.talla
                    : null
            ]
                .filter(Boolean)
                .join(" · ");

            return `
                <tr>
                    <td>
                        <strong>
                            ${escaparSalida(
                producto.codigo
            )}
                        </strong>
                    </td>

                    <td>
                        <span class="producto-nombre">
                            ${escaparSalida(
                producto.nombre
            )}
                        </span>

                        <span class="producto-secundario">
                            ${escaparSalida(detalles)}
                        </span>
                    </td>

                    <td>
                        ${producto.stockAlmacen}
                    </td>

                    <td>
                        <strong>
                            ${producto.cantidad}
                        </strong>
                    </td>

                    <td>
                        <button
                            type="button"
                            class="boton-tabla boton-quitar-carga"
                            data-accion="quitar"
                            data-id="${producto.idProducto}"
                        >
                            Quitar
                        </button>
                    </td>
                </tr>
            `;
        }).join("");

    const total =
        productosCarga.reduce(
            (acumulado, producto) =>
                acumulado
                + producto.cantidad,
            0
        );

    document
        .getElementById(
            "totalUnidadesCarga"
        )
        .textContent =
        total;
}

function controlarAccionCarga(evento) {

    const boton =
        evento.target.closest(
            "button[data-accion='quitar']"
        );

    if (!boton) {
        return;
    }

    const idProducto =
        Number(boton.dataset.id);

    productosCarga =
        productosCarga.filter(
            producto =>
                producto.idProducto
                !== idProducto
        );

    mostrarCarga();
}

function limpiarFormularioSalida() {

    document
        .getElementById("formSalida")
        .reset();

    productosCarga = [];

    mostrarCarga();
    colocarFechaActual();
}

function escaparSalida(valor) {

    const elemento =
        document.createElement("div");

    elemento.textContent =
        valor ?? "";

    return elemento.innerHTML;
}

function mostrarMensajeSalida(
    texto,
    tipo
) {

    const mensaje =
        document.getElementById(
            "mensajeSalida"
        );

    mensaje.textContent = texto;

    mensaje.className =
        tipo === "exito"
            ? "mensaje-sistema mensaje-exito"
            : "mensaje-sistema mensaje-error-producto";

    window.setTimeout(
        () => mensaje.classList.add("oculto"),
        4500
    );
}

async function guardarSalida(evento) {

    evento.preventDefault();

    if (productosCarga.length === 0) {

        mostrarMensajeSalida(
            "Agrega al menos un producto.",
            "error"
        );

        return;
    }

    const solicitud = {

        fechaSalida:
            document
                .getElementById("fechaSalida")
                .value,

        destino:
            document
                .getElementById("destino")
                .value
                .trim(),

        vendedor:
            document
                .getElementById("vendedor")
                .value
                .trim(),

        observaciones:
            document
                .getElementById(
                    "observacionesSalida"
                )
                .value
                .trim()
            || null,

        detalles:
            productosCarga.map(
                producto => ({
                    idProducto:
                        producto.idProducto,

                    cantidad:
                        producto.cantidad
                })
            )
    };

    const boton =
        document.getElementById(
            "btnGuardarSalida"
        );

    try {
        boton.disabled = true;
        boton.textContent =
            "Confirmando salida...";

        const respuesta =
            await fetch(
                "/api/salidas",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(solicitud)
                }
            );

        const resultado =
            await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.mensaje
                || "No se pudo registrar la salida"
            );
        }

        mostrarMensajeSalida(
            `Salida N.º ${resultado.idSalida} `
            + "registrada correctamente.",
            "exito"
        );

        limpiarFormularioSalida();

        await cargarProductosDisponibles();
        await cargarSalidas();

    } catch (error) {

        console.error(error);

        mostrarMensajeSalida(
            error.message,
            "error"
        );

    } finally {

        boton.disabled = false;
        boton.textContent =
            "Confirmar salida";
    }
}

async function cargarSalidas() {

    const tabla =
        document.getElementById(
            "tablaSalidas"
        );

    tabla.innerHTML = `
        <tr>
            <td colspan="7" class="tabla-vacia">
                Cargando salidas...
            </td>
        </tr>
    `;

    try {
        const respuesta =
            await fetch("/api/salidas");

        if (!respuesta.ok) {
            throw new Error(
                "No se pudieron cargar las salidas"
            );
        }

        salidasRegistradas =
            await respuesta.json();

        salidasRegistradas.sort(
            (a, b) =>
                b.idSalida - a.idSalida
        );

        aplicarFiltrosSalidas();

    } catch (error) {

        console.error(error);

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    No se pudieron cargar las salidas.
                </td>
            </tr>
        `;

        mostrarMensajeSalida(
            error.message,
            "error"
        );
    }
}

function aplicarFiltrosSalidas() {

    const texto =
        document
            .getElementById("buscadorSalidas")
            .value
            .trim()
            .toLowerCase();

    const estado =
        document
            .getElementById("filtroEstadoSalida")
            .value;

    const filtradas =
        salidasRegistradas.filter(
            salida => {

                const contenido = [
                    salida.idSalida,
                    salida.fechaSalida,
                    salida.destino,
                    salida.vendedor
                ]
                    .filter(Boolean)
                    .join(" ")
                    .toLowerCase();

                const coincideTexto =
                    contenido.includes(texto);

                const coincideEstado =
                    estado === "TODAS"
                    || salida.estado === estado;

                return coincideTexto
                    && coincideEstado;
            }
        );

    mostrarSalidas(filtradas);
}

function mostrarSalidas(salidas) {

    const tabla =
        document.getElementById(
            "tablaSalidas"
        );

    document
        .getElementById("contadorSalidas")
        .textContent =
        salidas.length;

    if (salidas.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    No se encontraron salidas.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML =
        salidas.map(salida => {

            const totalUnidades =
                (salida.detalles || [])
                    .reduce(
                        (total, detalle) =>
                            total
                            + detalle
                                .cantidadCargada,
                        0
                    );

            const claseEstado =
                salida.estado === "ABIERTA"
                    ? "estado-abierta"
                    : salida.estado === "CERRADA"
                        ? "estado-cerrada"
                        : "estado-cancelada";

            const botonCerrar =
                salida.estado === "ABIERTA"
                    ? `
                        <button
                            type="button"
                            class="boton-tabla boton-cerrar-salida"
                            data-accion="cerrar"
                            data-id="${salida.idSalida}"
                        >
                            Cerrar
                        </button>
                      `
                    : "";

            return `
                <tr>
                    <td>
                        <strong>
                            SAL-${String(
                salida.idSalida
            ).padStart(5, "0")}
                        </strong>
                    </td>

                    <td>
                        ${formatearFechaSalida(
                salida.fechaSalida
            )}
                    </td>

                    <td>
                        <span class="producto-nombre">
                            ${escaparSalida(
                salida.destino
            )}
                        </span>

                        <span class="producto-secundario">
                            ${escaparSalida(
                salida.observaciones
                || ""
            )}
                        </span>
                    </td>

                    <td>
                        ${escaparSalida(
                salida.vendedor
            )}
                    </td>

                    <td>
                        <strong>
                            ${totalUnidades}
                        </strong>
                    </td>

                    <td>
                        <span
                            class="insignia-estado ${claseEstado}"
                        >
                            ${salida.estado}
                        </span>
                    </td>

                    <td>
                        <div class="acciones-tabla">

                            <button
                                type="button"
                                class="boton-tabla boton-ver"
                                data-accion="ver"
                                data-id="${salida.idSalida}"
                            >
                                Ver Kardex
                            </button>

                            ${botonCerrar}

                        </div>
                    </td>
                </tr>
            `;
        }).join("");
}

function formatearFechaSalida(
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

function controlarAccionSalida(evento) {

    const boton =
        evento.target.closest(
            "button[data-accion]"
        );

    if (!boton) {
        return;
    }

    const idSalida =
        Number(boton.dataset.id);

    const accion =
        boton.dataset.accion;

    if (accion === "ver"
        || accion === "cerrar") {

        abrirKardex(idSalida);
    }
}

async function abrirKardex(idSalida) {

    try {
        const respuesta =
            await fetch(
                `/api/salidas/${idSalida}`
            );

        const resultado =
            await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.mensaje
                || "No se pudo abrir el Kardex"
            );
        }

        salidaSeleccionada = resultado;

        mostrarKardex(resultado);

        const panel =
            document.getElementById(
                "panelKardex"
            );

        panel.classList.remove("oculto");

        panel.scrollIntoView({
            behavior: "smooth"
        });

    } catch (error) {

        mostrarMensajeSalida(
            error.message,
            "error"
        );
    }
}

function mostrarKardex(salida) {

    document
        .getElementById("tituloKardex")
        .textContent =
        `Kardex SAL-${String(
            salida.idSalida
        ).padStart(5, "0")}`;

    document
        .getElementById("informacionKardex")
        .textContent =
        `${formatearFechaSalida(
            salida.fechaSalida
        )} · ${salida.destino} · `
        + `${salida.vendedor} · `
        + `${salida.estado}`;

    const salidaAbierta =
        salida.estado === "ABIERTA";

    document
        .getElementById(
            "accionesCierreSalida"
        )
        .classList.toggle(
            "oculto",
            !salidaAbierta
        );

    const tabla =
        document.getElementById(
            "tablaKardex"
        );

    if (!salida.detalles
        || salida.detalles.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    La salida no tiene productos.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML =
        salida.detalles.map(detalle => {

            const producto =
                detalle.producto;

            const cantidadContada =
                salidaAbierta
                    ? detalle.stockEsperado
                    : detalle.cantidadContada;

            const diferencia =
                salidaAbierta
                    ? 0
                    : detalle.diferencia ?? 0;

            const claseDiferencia =
                obtenerClaseDiferencia(
                    diferencia
                );

            const textoDiferencia =
                obtenerTextoDiferencia(
                    diferencia
                );

            const desactivado =
                salidaAbierta
                    ? ""
                    : "disabled";

            return `
                <tr
                    data-id-producto="${producto.idProducto}"
                    data-stock-esperado="${detalle.stockEsperado}"
                >
                    <td>
                        <span class="producto-nombre">
                            ${escaparSalida(
                producto.nombre
            )}
                        </span>

                        <span class="producto-secundario">
                            ${escaparSalida(
                producto.codigo
            )}
                        </span>
                    </td>

                    <td>
                        ${detalle.cantidadCargada}
                    </td>

                    <td>
                        ${detalle.cantidadVendida}
                    </td>

                    <td>
                        <strong>
                            ${detalle.stockEsperado}
                        </strong>
                    </td>

                    <td>
                        <input
                            type="number"
                            min="0"
                            step="1"
                            value="${cantidadContada ?? 0}"
                            class="entrada-conteo"
                            ${desactivado}
                        >
                    </td>

                    <td>
                        <span
                            class="${claseDiferencia}"
                            data-campo="diferencia"
                        >
                            ${textoDiferencia}
                        </span>
                    </td>

                    <td>
                        <input
                            type="text"
                            maxlength="300"
                            value="${escaparSalida(
                detalle.observaciones
                || ""
            )}"
                            placeholder="Observación"
                            class="entrada-observacion"
                            ${desactivado}
                        >
                    </td>
                </tr>
            `;
        }).join("");
}

function actualizarDiferenciaVisual(
    evento
) {

    if (!evento.target.classList
        .contains("entrada-conteo")) {

        return;
    }

    const fila =
        evento.target.closest("tr");

    const stockEsperado =
        Number(
            fila.dataset.stockEsperado
        );

    const cantidadContada =
        Number(evento.target.value);

    const diferencia =
        cantidadContada - stockEsperado;

    const elemento =
        fila.querySelector(
            "[data-campo='diferencia']"
        );

    elemento.className =
        obtenerClaseDiferencia(
            diferencia
        );

    elemento.textContent =
        obtenerTextoDiferencia(
            diferencia
        );
}

function obtenerClaseDiferencia(
    diferencia
) {

    if (diferencia < 0) {
        return "diferencia-faltante";
    }

    if (diferencia > 0) {
        return "diferencia-sobrante";
    }

    return "diferencia-correcta";
}

function obtenerTextoDiferencia(
    diferencia
) {

    if (diferencia < 0) {
        return `Faltan ${Math.abs(
            diferencia
        )}`;
    }

    if (diferencia > 0) {
        return `Sobran ${diferencia}`;
    }

    return "Conforme";
}

async function cerrarSalidaSeleccionada() {

    if (!salidaSeleccionada) {

        mostrarMensajeSalida(
            "Selecciona una salida.",
            "error"
        );

        return;
    }

    if (salidaSeleccionada.estado
        !== "ABIERTA") {

        mostrarMensajeSalida(
            "La salida ya está cerrada.",
            "error"
        );

        return;
    }

    const filas =
        document.querySelectorAll(
            "#tablaKardex tr[data-id-producto]"
        );

    const conteos = [];

    for (const fila of filas) {

        const idProducto =
            Number(
                fila.dataset.idProducto
            );

        const stockEsperado =
            Number(
                fila.dataset.stockEsperado
            );

        const cantidadContada =
            Number(
                fila.querySelector(
                    ".entrada-conteo"
                ).value
            );

        const observaciones =
            fila.querySelector(
                ".entrada-observacion"
            ).value.trim();

        if (!Number.isInteger(
            cantidadContada
        )
            || cantidadContada < 0) {

            mostrarMensajeSalida(
                "Revisa las cantidades contadas.",
                "error"
            );

            return;
        }

        const diferencia =
            cantidadContada
            - stockEsperado;

        if (diferencia !== 0
            && !observaciones) {

            mostrarMensajeSalida(
                "Debes escribir una observación "
                + "cuando falta o sobra mercadería.",
                "error"
            );

            return;
        }

        conteos.push({
            idProducto:
                idProducto,

            cantidadContada:
                cantidadContada,

            observaciones:
                observaciones || "Stock conforme"
        });
    }

    const confirmar =
        window.confirm(
            "¿Confirmas el cierre de la salida? "
            + "La mercadería contada volverá al almacén."
        );

    if (!confirmar) {
        return;
    }

    const boton =
        document.getElementById(
            "btnCerrarSalida"
        );

    try {
        boton.disabled = true;
        boton.textContent =
            "Cerrando salida...";

        const respuesta =
            await fetch(
                `/api/salidas/`
                + `${salidaSeleccionada.idSalida}`
                + "/cerrar",
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        conteos: conteos
                    })
                }
            );

        const resultado =
            await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.mensaje
                || "No se pudo cerrar la salida"
            );
        }

        salidaSeleccionada =
            resultado;

        mostrarKardex(resultado);

        mostrarMensajeSalida(
            "Salida cerrada correctamente. "
            + "El stock regresó al almacén.",
            "exito"
        );

        await cargarSalidas();
        await cargarProductosDisponibles();

    } catch (error) {

        console.error(error);

        mostrarMensajeSalida(
            error.message,
            "error"
        );

    } finally {

        boton.disabled = false;
        boton.textContent =
            "Cerrar salida y devolver al almacén";
    }
}