let listaProductos = [];

document.addEventListener(
    "DOMContentLoaded",
    () => {

        document
            .getElementById("buscadorProductos")
            .addEventListener(
                "input",
                aplicarFiltros
            );

        document
            .getElementById("filtroEstado")
            .addEventListener(
                "change",
                aplicarFiltros
            );

        document
            .getElementById("formProducto")
            .addEventListener(
                "submit",
                guardarProducto
            );

        document
            .getElementById("btnCancelar")
            .addEventListener(
                "click",
                limpiarFormulario
            );

        document
            .getElementById("btnNuevoProducto")
            .addEventListener(
                "click",
                () => {
                    limpiarFormulario();

                    document
                        .querySelector(
                            ".panel-formulario"
                        )
                        .scrollIntoView({
                            behavior: "smooth"
                        });
                }
            );

        document
            .getElementById("tablaProductos")
            .addEventListener(
                "click",
                controlarAccionTabla
            );

        cargarProductos();
    }
);

async function cargarProductos() {

    const tabla =
        document.getElementById("tablaProductos");

    tabla.innerHTML = `
        <tr>
            <td colspan="7" class="tabla-vacia">
                Cargando productos...
            </td>
        </tr>
    `;

    try {
        const respuesta =
            await fetch("/api/productos");

        if (!respuesta.ok) {
            throw new Error(
                "No se pudieron consultar los productos"
            );
        }

        listaProductos =
            await respuesta.json();

        aplicarFiltros();

    } catch (error) {

        console.error(error);

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    No se pudieron cargar los productos.
                </td>
            </tr>
        `;

        mostrarMensaje(
            "No se pudieron cargar los productos.",
            "error"
        );
    }
}

function aplicarFiltros() {

    const texto =
        document
            .getElementById("buscadorProductos")
            .value
            .trim()
            .toLowerCase();

    const estado =
        document
            .getElementById("filtroEstado")
            .value;

    const productosFiltrados =
        listaProductos.filter(producto => {

            const contenido = [
                producto.codigo,
                producto.nombre,
                producto.categoria,
                producto.marca,
                producto.modelo,
                producto.color,
                producto.talla
            ]
                .filter(Boolean)
                .join(" ")
                .toLowerCase();

            const coincideTexto =
                contenido.includes(texto);

            let coincideEstado = true;

            if (estado === "ACTIVOS") {
                coincideEstado =
                    producto.activo === true;
            }

            if (estado === "INACTIVOS") {
                coincideEstado =
                    producto.activo === false;
            }

            if (estado === "STOCK_BAJO") {
                coincideEstado =
                    producto.activo === true
                    && producto.stockAlmacen
                    <= producto.stockMinimo;
            }

            return coincideTexto
                && coincideEstado;
        });

    mostrarProductos(productosFiltrados);
}

function mostrarProductos(productos) {

    const tabla =
        document.getElementById("tablaProductos");

    const contador =
        document.getElementById(
            "contadorProductos"
        );

    contador.textContent =
        productos.length;

    if (productos.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    No se encontraron productos.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML =
        productos.map(producto => {

            const stockBajo =
                producto.stockAlmacen
                <= producto.stockMinimo;

            const claseStock =
                stockBajo
                    ? "stock-alerta"
                    : "stock-normal";

            const estadoProducto =
                producto.activo
                    ? `
                        <span class="insignia-estado estado-activo">
                            Activo
                        </span>
                      `
                    : `
                        <span class="insignia-estado estado-inactivo">
                            Inactivo
                        </span>
                      `;

            const detalles = [
                producto.categoria,
                producto.marca,
                producto.modelo,
                producto.color
                    ? "Color: " + producto.color
                    : null,
                producto.talla
                    ? "Talla: " + producto.talla
                    : null
            ]
                .filter(Boolean)
                .join(" · ");

            const botonEstado =
                producto.activo
                    ? `
                        <button
                            type="button"
                            class="boton-tabla boton-desactivar"
                            data-accion="desactivar"
                            data-id="${producto.idProducto}"
                        >
                            Desactivar
                        </button>
                      `
                    : `
                        <button
                            type="button"
                            class="boton-tabla boton-activar"
                            data-accion="activar"
                            data-id="${producto.idProducto}"
                        >
                            Activar
                        </button>
                      `;

            return `
                <tr>
                    <td>
                        <strong>
                            ${escapar(producto.codigo)}
                        </strong>
                    </td>

                    <td>
                        <span class="producto-nombre">
                            ${escapar(producto.nombre)}
                        </span>

                        <span class="producto-secundario">
                            ID: ${producto.idProducto}
                        </span>
                    </td>

                    <td>
                        ${escapar(detalles || "Sin detalles")}
                    </td>

                    <td class="precio-producto">
                        ${formatoMoneda(
                            producto.precioVenta
                        )}
                    </td>

                    <td>
                        <span class="${claseStock}">
                            ${producto.stockAlmacen}
                        </span>

                        <span class="producto-secundario">
                            Mínimo: ${producto.stockMinimo}
                        </span>
                    </td>

                    <td>
                        ${estadoProducto}
                    </td>

                    <td>
                        <div class="acciones-tabla">

                            <button
                                type="button"
                                class="boton-tabla boton-editar"
                                data-accion="editar"
                                data-id="${producto.idProducto}"
                            >
                                Editar
                            </button>

                            ${botonEstado}

                        </div>
                    </td>
                </tr>
            `;
        }).join("");
}

function formatoMoneda(valor) {

    return new Intl.NumberFormat(
        "es-PE",
        {
            style: "currency",
            currency: "PEN"
        }
    ).format(Number(valor ?? 0));
}

function escapar(valor) {

    const elemento =
        document.createElement("div");

    elemento.textContent =
        valor ?? "";

    return elemento.innerHTML;
}

function mostrarMensaje(texto, tipo) {

    const mensaje =
        document.getElementById(
            "mensajeProducto"
        );

    mensaje.textContent = texto;

    mensaje.className =
        tipo === "exito"
            ? "mensaje-sistema mensaje-exito"
            : "mensaje-sistema mensaje-error-producto";

    window.setTimeout(
        () => mensaje.classList.add("oculto"),
        4000
    );
}

async function guardarProducto(evento) {

    evento.preventDefault();

    const idProducto =
        document
            .getElementById("idProducto")
            .value;

    const productoExistente =
        listaProductos.find(
            producto =>
                String(producto.idProducto)
                === String(idProducto)
        );

    const activo =
        productoExistente
            ? productoExistente.activo
            : true;

    const datos =
        obtenerDatosFormulario(activo);

    const editando =
        Boolean(idProducto);

    const direccion =
        editando
            ? `/api/productos/${idProducto}`
            : "/api/productos";

    const metodo =
        editando
            ? "PUT"
            : "POST";

    const boton =
        document.getElementById(
            "btnGuardarProducto"
        );

    try {
        boton.disabled = true;

        boton.textContent =
            editando
                ? "Guardando cambios..."
                : "Registrando...";

        const respuesta =
            await fetch(
                direccion,
                {
                    method: metodo,

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify(datos)
                }
            );

        const resultado =
            await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.mensaje
                || "No se pudo guardar el producto"
            );
        }

        mostrarMensaje(
            editando
                ? "Producto modificado correctamente."
                : "Producto registrado correctamente.",
            "exito"
        );

        limpiarFormulario();

        await cargarProductos();

    } catch (error) {

        console.error(error);

        mostrarMensaje(
            error.message,
            "error"
        );

    } finally {

        boton.disabled = false;
        boton.textContent = "Guardar producto";
    }
}

function obtenerDatosFormulario(activo) {

    return {
        codigo:
            document
                .getElementById("codigo")
                .value
                .trim(),

        nombre:
            document
                .getElementById("nombre")
                .value
                .trim(),

        categoria:
            valorOpcional("categoria"),

        marca:
            valorOpcional("marca"),

        modelo:
            valorOpcional("modelo"),

        color:
            valorOpcional("color"),

        talla:
            valorOpcional("talla"),

        precioVenta:
            Number(
                document
                    .getElementById("precioVenta")
                    .value
            ),

        stockAlmacen:
            Number(
                document
                    .getElementById("stockAlmacen")
                    .value
            ),

        stockMinimo:
            Number(
                document
                    .getElementById("stockMinimo")
                    .value
            ),

        activo: activo
    };
}

function valorOpcional(idElemento) {

    const valor =
        document
            .getElementById(idElemento)
            .value
            .trim();

    return valor || null;
}

function controlarAccionTabla(evento) {

    const boton =
        evento.target.closest(
            "button[data-accion]"
        );

    if (!boton) {
        return;
    }

    const idProducto =
        Number(boton.dataset.id);

    const accion =
        boton.dataset.accion;

    if (accion === "editar") {
        editarProducto(idProducto);
    }

    if (accion === "desactivar") {
        desactivarProducto(idProducto);
    }

    if (accion === "activar") {
        activarProducto(idProducto);
    }
}

function editarProducto(idProducto) {

    const producto =
        listaProductos.find(
            elemento =>
                elemento.idProducto === idProducto
        );

    if (!producto) {
        mostrarMensaje(
            "No se encontró el producto.",
            "error"
        );

        return;
    }

    colocarValor(
        "idProducto",
        producto.idProducto
    );

    colocarValor(
        "codigo",
        producto.codigo
    );

    colocarValor(
        "nombre",
        producto.nombre
    );

    colocarValor(
        "categoria",
        producto.categoria
    );

    colocarValor(
        "marca",
        producto.marca
    );

    colocarValor(
        "modelo",
        producto.modelo
    );

    colocarValor(
        "color",
        producto.color
    );

    colocarValor(
        "talla",
        producto.talla
    );

    colocarValor(
        "precioVenta",
        producto.precioVenta
    );

    colocarValor(
        "stockAlmacen",
        producto.stockAlmacen
    );

    colocarValor(
        "stockMinimo",
        producto.stockMinimo
    );

    document
        .getElementById("tituloFormulario")
        .textContent =
            "Modificar producto";

    document
        .getElementById("btnGuardarProducto")
        .textContent =
            "Guardar cambios";

    document
        .querySelector(".panel-formulario")
        .scrollIntoView({
            behavior: "smooth"
        });
}

function colocarValor(idElemento, valor) {

    document
        .getElementById(idElemento)
        .value =
            valor ?? "";
}

async function desactivarProducto(
    idProducto
) {

    const producto =
        listaProductos.find(
            elemento =>
                elemento.idProducto === idProducto
        );

    if (!producto) {
        return;
    }

    const confirmar =
        window.confirm(
            `¿Deseas desactivar "${producto.nombre}"?`
        );

    if (!confirmar) {
        return;
    }

    try {
        const respuesta =
            await fetch(
                `/api/productos/${idProducto}`,
                {
                    method: "DELETE"
                }
            );

        const resultado =
            await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.mensaje
                || "No se pudo desactivar"
            );
        }

        mostrarMensaje(
            "Producto desactivado correctamente.",
            "exito"
        );

        await cargarProductos();

    } catch (error) {

        mostrarMensaje(
            error.message,
            "error"
        );
    }
}

async function activarProducto(idProducto) {

    const producto =
        listaProductos.find(
            elemento =>
                elemento.idProducto === idProducto
        );

    if (!producto) {
        return;
    }

    const datos = {
        codigo: producto.codigo,
        nombre: producto.nombre,
        categoria: producto.categoria,
        marca: producto.marca,
        modelo: producto.modelo,
        color: producto.color,
        talla: producto.talla,
        precioVenta: producto.precioVenta,
        stockAlmacen: producto.stockAlmacen,
        stockMinimo: producto.stockMinimo,
        activo: true
    };

    try {
        const respuesta =
            await fetch(
                `/api/productos/${idProducto}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify(datos)
                }
            );

        const resultado =
            await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.mensaje
                || "No se pudo activar"
            );
        }

        mostrarMensaje(
            "Producto activado correctamente.",
            "exito"
        );

        await cargarProductos();

    } catch (error) {

        mostrarMensaje(
            error.message,
            "error"
        );
    }
}

function limpiarFormulario() {

    document
        .getElementById("formProducto")
        .reset();

    document
        .getElementById("idProducto")
        .value = "";

    document
        .getElementById("tituloFormulario")
        .textContent =
            "Registrar producto";

    document
        .getElementById("btnGuardarProducto")
        .textContent =
            "Guardar producto";
}