let listaClientes = [];
document.addEventListener(
    "DOMContentLoaded",
    () => {

        document
            .getElementById("buscadorClientes")
            .addEventListener(
                "input",
                aplicarFiltrosClientes
            );

        document
            .getElementById("filtroEstadoCliente")
            .addEventListener(
                "change",
                aplicarFiltrosClientes
            );

        document
            .getElementById("formCliente")
            .addEventListener(
                "submit",
                guardarCliente
            );

        document
            .getElementById("btnCancelarCliente")
            .addEventListener(
                "click",
                limpiarFormularioCliente
            );

        document
            .getElementById("btnNuevoCliente")
            .addEventListener(
                "click",
                () => {
                    limpiarFormularioCliente();

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
            .getElementById("tablaClientes")
            .addEventListener(
                "click",
                controlarAccionCliente
            );

        cargarClientes();
    }
);

async function cargarClientes() {

    const tabla =
        document.getElementById(
            "tablaClientes"
        );

    tabla.innerHTML = `
        <tr>
            <td colspan="7" class="tabla-vacia">
                Cargando clientes...
            </td>
        </tr>
    `;

    try {
        const respuesta =
            await fetch("/api/clientes");

        if (!respuesta.ok) {
            throw new Error(
                "No se pudieron consultar los clientes"
            );
        }

        listaClientes =
            await respuesta.json();

        aplicarFiltrosClientes();

    } catch (error) {

        console.error(error);

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    No se pudieron cargar los clientes.
                </td>
            </tr>
        `;

        mostrarMensajeCliente(
            "No se pudieron cargar los clientes.",
            "error"
        );
    }
}

function aplicarFiltrosClientes() {

    const texto =
        document
            .getElementById("buscadorClientes")
            .value
            .trim()
            .toLowerCase();

    const estado =
        document
            .getElementById("filtroEstadoCliente")
            .value;

    const clientesFiltrados =
        listaClientes.filter(cliente => {

            const nombreCompleto = [
                cliente.nombres,
                cliente.apellidoPaterno,
                cliente.apellidoMaterno
            ]
                .filter(Boolean)
                .join(" ");

            const contenido = [
                cliente.codigoCliente,
                nombreCompleto,
                cliente.dni,
                cliente.celular,
                cliente.direccion,
                cliente.distrito,
                cliente.zona,
                cliente.referencia
            ]
                .filter(Boolean)
                .join(" ")
                .toLowerCase();

            const coincideTexto =
                contenido.includes(texto);

            let coincideEstado = true;

            if (estado === "ACTIVOS") {
                coincideEstado =
                    cliente.activo === true;
            }

            if (estado === "INACTIVOS") {
                coincideEstado =
                    cliente.activo === false;
            }

            return coincideTexto
                && coincideEstado;
        });

    mostrarClientes(clientesFiltrados);
}

function mostrarClientes(clientes) {

    const tabla =
        document.getElementById(
            "tablaClientes"
        );

    document
        .getElementById("contadorClientes")
        .textContent =
            clientes.length;

    if (clientes.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="7" class="tabla-vacia">
                    No se encontraron clientes.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML =
        clientes.map(cliente => {

            const nombreCompleto = [
                cliente.nombres,
                cliente.apellidoPaterno,
                cliente.apellidoMaterno
            ]
                .filter(Boolean)
                .join(" ");

            const contacto = [
                cliente.dni
                    ? "DNI: " + cliente.dni
                    : null,

                cliente.celular
                    ? "Cel: " + cliente.celular
                    : null
            ]
                .filter(Boolean)
                .join(" · ");

            const ubicacion = [
                cliente.direccion,
                cliente.distrito,
                cliente.zona
            ]
                .filter(Boolean)
                .join(" · ");

            const estadoCliente =
                cliente.activo
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

            const botonEstado =
                cliente.activo
                    ? `
                        <button
                            type="button"
                            class="boton-tabla boton-desactivar"
                            data-accion="desactivar"
                            data-id="${cliente.idCliente}"
                        >
                            Desactivar
                        </button>
                      `
                    : `
                        <button
                            type="button"
                            class="boton-tabla boton-activar"
                            data-accion="activar"
                            data-id="${cliente.idCliente}"
                        >
                            Activar
                        </button>
                      `;

            return `
                <tr>
                    <td>
                        <strong>
                            ${escaparCliente(
                                cliente.codigoCliente
                            )}
                        </strong>

                        <span class="producto-secundario">
                            ID: ${cliente.idCliente}
                        </span>
                    </td>

                    <td>
                        <span class="producto-nombre">
                            ${escaparCliente(
                                nombreCompleto
                            )}
                        </span>

                        <span class="producto-secundario">
                            ${escaparCliente(
                                cliente.puestoTrabajo
                                || "Sin trabajo registrado"
                            )}
                        </span>
                    </td>

                    <td>
                        ${escaparCliente(
                            contacto
                            || "Sin contacto"
                        )}
                    </td>

                    <td>
                        ${escaparCliente(
                            ubicacion
                            || "Sin dirección"
                        )}

                        <span class="producto-secundario">
                            ${escaparCliente(
                                cliente.referencia
                                || ""
                            )}
                        </span>
                    </td>

                    <td>
                        ${formatearFechaCliente(
                            cliente.fechaRegistro
                        )}
                    </td>

                    <td>
                        ${estadoCliente}
                    </td>

                    <td>
                        <div class="acciones-tabla">

                            <button
                                type="button"
                                class="boton-tabla boton-editar"
                                data-accion="editar"
                                data-id="${cliente.idCliente}"
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

function formatearFechaCliente(fechaTexto) {

    if (!fechaTexto) {
        return "Sin fecha";
    }

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            dateStyle: "medium"
        }
    ).format(
        new Date(fechaTexto)
    );
}

function escaparCliente(valor) {

    const elemento =
        document.createElement("div");

    elemento.textContent =
        valor ?? "";

    return elemento.innerHTML;
}

function mostrarMensajeCliente(
    texto,
    tipo
) {

    const mensaje =
        document.getElementById(
            "mensajeCliente"
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

async function guardarCliente(evento) {

    evento.preventDefault();

    const idCliente =
        document
            .getElementById("idCliente")
            .value;

    const editando =
        Boolean(idCliente);

    const datos =
        obtenerDatosCliente();

    const direccion =
        editando
            ? `/api/clientes/${idCliente}`
            : "/api/clientes";

    const metodo =
        editando
            ? "PUT"
            : "POST";

    const boton =
        document.getElementById(
            "btnGuardarCliente"
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
                || "No se pudo guardar el cliente"
            );
        }

        mostrarMensajeCliente(
            editando
                ? "Cliente modificado correctamente."
                : "Cliente registrado correctamente.",
            "exito"
        );

        limpiarFormularioCliente();

        await cargarClientes();

    } catch (error) {

        console.error(error);

        mostrarMensajeCliente(
            error.message,
            "error"
        );

    } finally {

        boton.disabled = false;
        boton.textContent =
            "Guardar cliente";
    }
}

function obtenerDatosCliente() {

    return {
        nombres:
            valorCliente("nombres"),

        apellidoPaterno:
            valorCliente("apellidoPaterno"),

        apellidoMaterno:
            valorOpcionalCliente(
                "apellidoMaterno"
            ),

        dni:
            valorOpcionalCliente("dni"),

        celular:
            valorOpcionalCliente("celular"),

        direccion:
            valorOpcionalCliente("direccion"),

        distrito:
            valorOpcionalCliente("distrito"),

        zona:
            valorOpcionalCliente("zona"),

        referencia:
            valorOpcionalCliente("referencia"),

        puestoTrabajo:
            valorOpcionalCliente(
                "puestoTrabajo"
            ),

        activo: true
    };
}

function valorCliente(idElemento) {

    return document
        .getElementById(idElemento)
        .value
        .trim();
}

function valorOpcionalCliente(
    idElemento
) {

    const valor =
        valorCliente(idElemento);

    return valor || null;
}

function controlarAccionCliente(evento) {

    const boton =
        evento.target.closest(
            "button[data-accion]"
        );

    if (!boton) {
        return;
    }

    const idCliente =
        Number(boton.dataset.id);

    const accion =
        boton.dataset.accion;

    if (accion === "editar") {
        editarCliente(idCliente);
    }

    if (accion === "desactivar") {
        desactivarCliente(idCliente);
    }

    if (accion === "activar") {
        activarCliente(idCliente);
    }
}

function editarCliente(idCliente) {

    const cliente =
        listaClientes.find(
            elemento =>
                elemento.idCliente === idCliente
        );

    if (!cliente) {
        mostrarMensajeCliente(
            "No se encontró el cliente.",
            "error"
        );

        return;
    }

    colocarValorCliente(
        "idCliente",
        cliente.idCliente
    );

    colocarValorCliente(
        "nombres",
        cliente.nombres
    );

    colocarValorCliente(
        "apellidoPaterno",
        cliente.apellidoPaterno
    );

    colocarValorCliente(
        "apellidoMaterno",
        cliente.apellidoMaterno
    );

    colocarValorCliente(
        "dni",
        cliente.dni
    );

    colocarValorCliente(
        "celular",
        cliente.celular
    );

    colocarValorCliente(
        "direccion",
        cliente.direccion
    );

    colocarValorCliente(
        "distrito",
        cliente.distrito
    );

    colocarValorCliente(
        "zona",
        cliente.zona
    );

    colocarValorCliente(
        "referencia",
        cliente.referencia
    );

    colocarValorCliente(
        "puestoTrabajo",
        cliente.puestoTrabajo
    );

    document
        .getElementById(
            "tituloFormularioCliente"
        )
        .textContent =
            "Modificar cliente";

    document
        .getElementById(
            "btnGuardarCliente"
        )
        .textContent =
            "Guardar cambios";

    document
        .querySelector(".panel-formulario")
        .scrollIntoView({
            behavior: "smooth"
        });
}

function colocarValorCliente(
    idElemento,
    valor
) {

    document
        .getElementById(idElemento)
        .value =
            valor ?? "";
}

async function desactivarCliente(
    idCliente
) {

    const cliente =
        listaClientes.find(
            elemento =>
                elemento.idCliente === idCliente
        );

    if (!cliente) {
        return;
    }

    const nombreCompleto = [
        cliente.nombres,
        cliente.apellidoPaterno,
        cliente.apellidoMaterno
    ]
        .filter(Boolean)
        .join(" ");

    const confirmar =
        window.confirm(
            `¿Deseas desactivar a "${nombreCompleto}"?`
        );

    if (!confirmar) {
        return;
    }

    try {
        const respuesta =
            await fetch(
                `/api/clientes/${idCliente}`,
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

        mostrarMensajeCliente(
            "Cliente desactivado correctamente.",
            "exito"
        );

        await cargarClientes();

    } catch (error) {

        mostrarMensajeCliente(
            error.message,
            "error"
        );
    }
}

async function activarCliente(idCliente) {

    try {
        const respuesta =
            await fetch(
                `/api/clientes/${idCliente}/activar`,
                {
                    method: "PUT"
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

        mostrarMensajeCliente(
            "Cliente activado correctamente.",
            "exito"
        );

        await cargarClientes();

    } catch (error) {

        mostrarMensajeCliente(
            error.message,
            "error"
        );
    }
}

function limpiarFormularioCliente() {

    document
        .getElementById("formCliente")
        .reset();

    document
        .getElementById("idCliente")
        .value = "";

    document
        .getElementById(
            "tituloFormularioCliente"
        )
        .textContent =
            "Registrar cliente";

    document
        .getElementById(
            "btnGuardarCliente"
        )
        .textContent =
            "Guardar cliente";
}