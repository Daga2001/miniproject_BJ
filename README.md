# MiniProyecto_5_Black_Jack
Our last miniproject of IP.

## Description (Spanish)

El juego inicia cuando los tres jugadores (clientes), esté conectados. Al llegar un jugador (cliente), si la mesa no está completa, este deberá esperar a qué llegen los demás jugadores.
Una vez llega el úlitmo de los clientes, cada cliente podrá ver de manera gráfica la mesa de juego e iniciarán los turnos por jugador según se indica en las reglas del juego.
Cada jugador en su turno podrá pedir cartas (si su mano es menor que 21) o plantar.
Si el jugador se pasa de 21 significa que vuela, por lo cual pierde.
Cuando un jugador plante o vuele termina su turno y podrá seguir el siguiente jugador.

Para el caso del Dealer, quien siempre será el último en jugar, se seguirá lo que se indica en las reglas del juego.


Una vez termine de jugar el Dealer se Terminará la Ronda de Juego y se debe determinar si el Dealer deberá pagar o recoger sus ganancias de la mesa según sean los resultados:
Si el jugador empata con el Dealer recupera su apuesta
Si el jugador pierde con el dealer pierde su apuesta
Si el jugador gana con Black Jack, el Dealer le debe pagar en razón de 3 a 2 (3 pesos por cada 2 apostados, esto es, si apostó 10 el Dealer le pagará 15)
Si el jugador gana con 21 (por sumatoria de cartas) u otro valor el Dealer le paga a razón de 1 a 1.



Finalmente, se deberá poder reiniciar una nueva ronda de juego.

## Rules (Spanish)

- **El servidor debe gestionar tres jugadores (clientes) que juegan Black Jack contra el dealer. (Falta)**
- El juego inicia cuando todos jugadores (clientes) están conectados al servidor.
- **Al conectarse cada jugador (cliente) suministra su identificador y un valor de apuesta al servidor. (Falta)**
- El servidor controlará que cada jugador solo juegue cuando le corresponde su turno e irá rotando el turno entre jugadores.
- El turno será según el orden de conexión, es decir, el primero en llegar tendrá el primer turno, y el último en llegar tendrá el último turno.
- **Cada jugador (cliente) deberá visualizar de manera gráfica, la mesa de juego, la cual incluye, sus cartas, las cartas de los otros jugadores (incluido el dealer) y los mensajes con el estado del juego. (Falta)**
- El servidor no tendrá Interfaz gráfica.

El dealer será emulado por el servidor (es decir, no es un jugador conectado) y responde a la siguiente regla:

- **Con 16 o menos debe pedir cartac (Verificar)**
- **Con 17 o mas planta. (Verificar)**

## Requirements (Spanish)

Dado que para este proyecto se parte de un programa que ya define la estructura Cliente/Servidor, las funcionalidades a evaluar serán las siguientes:

- El servidor gestiona tres jugadores
- Cada cliente además del identificador, deberá suministrar al servidor el valor de apuesta inicial
- Las cartas en el cliente se deben visualizar de manera gráfica y se debe visualizar las apuestas de cada jugador
- Se debe terminar la ronda de juego, esto es, establecer si el Dealer deberá pagar o recoger sus ganancias de la mesa según sean los resultados:
- Si el jugador empata con el Dealer recupera su apuesta
- Si el jugador pierde con el dealer pierde su apuesta
- Si el jugador gana con Black Jack, el Dealer le debe pagar en razón de 3 a 2 (3 pesos por cada 2 apostados, esto es, si apostó 10 el Dealer le pagará 15)
- Si el jugador gana con 21 (por sumatoria de cartas) u otro valor el Dealer le paga a razón de 1 a 1.
Para esto, ustede podrá usar la zona de mensajes y allí mostrarán los resultados de la ronda de juego.
Iniciar nueva ronda de juego (consiste en repartir nuevas cartas y reinciar el orden de turnos según lo indica las reglas de juego)


- **Note:** Para este proyecto no se debe generar ningún diagrama.

## Update 1.1

- Se implementa el juego con tres jugadores, lo cual quedaria con cuatro, contando el dealer (Se añadió un tercer cliente).
- Se añadio un tercer panel para el jugador 3, quedando 5 paneles a la vista de cada jugador, uno por cada jugador, y los otros dos para el dealer y los mensajes.

## Update 1.2

- Se han añadido graficos al juego, ahora las cartas no son textos (Strings), sino imagenes.

## Update 1.3

- Ahora se han implementado apuestas en el juego.
- Cada jugador puede apostar cuanto quiera al iniciar el juego.

## Update 1.4

- Se mejoraron los graficos de la mesa de juego.
- La mesa tiene una imagen de fondo, y cada panel de los jugadores tiene un fondo semitransparente

## Update 1.5

- El juego ya es capaz de calcular las ganancias o perdidas de los jugadores.
