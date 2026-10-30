# LavaCampus
App de lavandería universitaria (Kotlin + Jetpack Compose + Material 3, MVVM + Repository, Room, DataStore).

## Descripción
Dos perfiles en el mismo dispositivo: Estudiante (arma pedidos, sigue su ropa, recoge en casillero) y Administrador (avanza estados, asigna casillero, cobra, gestiona tarifas y clientes). Ambos comparten la base Room.

## Estructura de paquetes
<img width="247" height="435" alt="image" src="https://github.com/user-attachments/assets/9ddaab98-3097-441d-b59f-66ce04e2359e" />
<img width="206" height="329" alt="image" src="https://github.com/user-attachments/assets/8291f1e5-8e58-4303-962f-8b394505cc5b" />
<img width="194" height="215" alt="image" src="https://github.com/user-attachments/assets/8d081064-26c8-4feb-868e-2c77161ae395" />


## Arquitectura
View (Composable) → ViewModel (StateFlow + reglas) → LavaRepository → DAO → SQLite. DataStore solo guarda preferencias.

## Capturas
Estudiante:
<img width="210" height="451" alt="image" src="https://github.com/user-attachments/assets/a329f250-8fdb-45c9-8c80-c86dde4c4c55" />
<img width="207" height="451" alt="image" src="https://github.com/user-attachments/assets/71e18670-90f6-49a1-8225-812675549123" />
<img width="205" height="454" alt="image" src="https://github.com/user-attachments/assets/c5e3c2b2-becc-4cac-bde9-e9901fc12983" />


Administrador:
<img width="208" height="456" alt="image" src="https://github.com/user-attachments/assets/6a41497b-0f79-4690-9d5b-67abb32ed077" />
<img width="206" height="450" alt="image" src="https://github.com/user-attachments/assets/d492ecc3-44e8-4cb3-9819-bf125e106a71" />
<img width="209" height="452" alt="image" src="https://github.com/user-attachments/assets/17987ed7-1830-4f15-8df3-97460eea4ce8" />


## Para pensar
1. ¿Por qué el pedido tiene los detalles en otra tabla? Porque un pedido puede incluir varios servicios (relación uno a muchos); evita columnas repetidas y permite consultar ingresos por servicio con GROUP BY.
Un pedido puede incluir varios servicios, por ejemplo lavado, planchado y lavado en seco. Es una relación uno a muchos: un pedido tiene muchos detalles. Si metieras todo en la tabla pedidos, tendrías columnas repetidas
(servicio1, servicio2...) y un límite de servicios por pedido. En una tabla aparte cada línea es una fila, así que no hay límite y se consulta fácil, por ejemplo los ingresos por servicio con GROUP BY.

2. ¿Por qué guardar precioUnitario y subtotal en el detalle? Es una “foto” del momento: si sube el precio, los pedidos antiguos y los ingresos del panel no cambian.
Porque son una foto del precio en el momento de pedir. Si después el administrador cambia la tarifa del servicio, los pedidos antiguos conservan lo que realmente se cobró. Así el historial, los totales y los ingresos del panel
no cambian solos. Si solo guardaras el servicioId, un cambio de tarifa alteraría el pasado.
