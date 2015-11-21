/**
 * Created by kortatu on 17/11/15.
 */

class CAndK {

    static enum TIPO_ESTRATEGIA  {ATAQUE, CARAVANAS, INPUT}
    static TIPO_ESTRATEGIA estrategia = TIPO_ESTRATEGIA.CARAVANAS
    static final int initGold = 5
    static final int costCaravan = 2
    static final int costKnight = 5
    static final int inteligencia = 15
    static final int bonusComercio = 20
    static final int bonusAtaque = 0
    static final List<Integer> percMerchant = [inteligencia * 2 + bonusComercio, 40]
    static final List<Integer> percAttack = [inteligencia*2 + bonusAtaque, 75]
    static final int turnos = 5
    static final int MAX_CARAVANS = 7
    static final int MAX_KNIGHTS = 3
    static final int PRECIO_KNIGHT_FINAL = 1
    static final int BENEFICIO_MERCHANT_EXITO = 5
    static final int BENEFICIO_MERCHANT_FALLO = 1

    public static void main(String[] args) {
        def ganadas = [0,0]
        if (args.length == 0) {
            1000.times {
                aGame(ganadas)
            }
        } else {
            5.times {
                estrategia = TIPO_ESTRATEGIA.INPUT
                aGame(ganadas)
            }
        }
        println "Ganadas $ganadas"
    }

    private static void aGame(List<Integer> ganadas) {
        def gold = [initGold, initGold]
        def caravans = [0, 0]
        def knights = [0, 0]
        def turno = 1
        def continua = true;

        while (turno <= turnos && continua) {
            println("****TURNO $turno ******")
//                printPartida(gold, caravans, knights, "Compras")
            estrategiaCompraJugador(turno, gold, caravans, knights)
            estrategiaCompraBartol2(turno, gold, caravans, knights)
            printPartida(gold, caravans, knights, "Antes de comercio")
            merchants(percMerchant, caravans, gold)
            printPartida(gold, caravans, knights, "Antes de ataques")
            attacks(percAttack, caravans, knights, gold)
            printPartida(gold, caravans, knights, "fin turno")
            if (gold[0] < 0) {
                println "GANA BARTOL"
                ganadas[1] = ganadas[1] + 1
                continua = false;
            } else {
                if (gold[1] < 0) {
                    println "GANA JUGADOR!!"
                    ganadas[0] = ganadas[0] + 1
                    continua = false;
                }
            }
            turno++;
        }
        if (continua) {
            gold[0] = gold[0] + PRECIO_KNIGHT_FINAL * knights[0]
            gold[1] = gold[1] + PRECIO_KNIGHT_FINAL * knights[1]
            println "Oro total. Jugador ${gold[0]} Bartol ${gold[1]}"
            if (gold[0] > gold[1]) {
                println "GANA JUGADOR!!"
                ganadas[0] = ganadas[0] + 1
            } else {
                println "GANA BARTOL!!"
                ganadas[1] = ganadas[1] + 1
            }
        }
    }

    static def estrategiaCompraJugador(int turno, ArrayList<Integer> gold, ArrayList<Integer> caravans, ArrayList<Integer> knights) {
        if (estrategia == TIPO_ESTRATEGIA.ATAQUE)
            estrategiaAtaque(turno, gold, caravans, knights)
        else if (estrategia == TIPO_ESTRATEGIA.CARAVANAS)
            estrategiaCaravans(turno, gold, caravans, knights)
        else
           estrategiaInput(turno, gold, caravans, knights)
    }

    static def estrategiaInput(int turn, ArrayList<Integer> gold, ArrayList<Integer> caravans, ArrayList<Integer> knights) {
        Scanner sc = new Scanner(System.in)

        def numJugador = 0
        def maxCaravans =Math.min(MAX_CARAVANS, (gold[numJugador]/2).toInteger())
        if (maxCaravans >0 ) {
            println("Caravanas [0-$maxCaravans]");
            def choose = sc.nextInt()
            println "Chosen $choose"
            if (choose<=maxCaravans)
                choose.times { compraCaravan(gold, numJugador, caravans) }
            def maxKnights =Math.min(3, (gold[numJugador]/5).toInteger())
            if (maxKnights>0) {
                println("Knights [0-$maxKnights]");
                choose = sc.nextInt()
                if (choose <= maxKnights)
                    choose.times { compraKnight(gold, numJugador, knights) }
            }
        }
    }

    private
    static void estrategiaAtaque(int turno, ArrayList<Integer> gold, ArrayList<Integer> caravans, ArrayList<Integer> knights) {
        int numJugador = 0
        if (turno == 1) {
            compraCaravan(gold, numJugador, caravans)
            compraCaravan(gold, numJugador, caravans)
        } else {
            MAX_KNIGHTS.times {
                if (gold[numJugador] >= 7) {
                    compraKnight(gold, numJugador, knights)
                }
            }

            int posible = (gold[numJugador].intValue())/costCaravan
            posible = Math.min(posible, MAX_CARAVANS)
            println "El jugador puede comprar $posible caravanas"
            posible.times {
                compraCaravan(gold, numJugador, caravans)
            }
        }
    }


    private
    static void estrategiaCaravans(int turno, ArrayList<Integer> gold, ArrayList<Integer> caravans, ArrayList<Integer> knights) {
        int numJugador = 0
        if ((turno < 4) && gold[numJugador]>12)
            compraKnight(gold,numJugador,knights)
        int posible = (gold[numJugador].intValue())/costCaravan
        posible = Math.min(posible, MAX_CARAVANS)
        println "El jugador puede comprar $posible caravanas"
        posible.times {
            compraCaravan(gold, numJugador, caravans)
        }
    }

    static def estrategiaCompraBartol(int turno, ArrayList<Integer> gold, ArrayList<Integer> caravans, ArrayList<Integer> knights) {
        int numJugador = 1
        if (turno == 1) {
            compraCaravan(gold, numJugador, caravans)
            compraCaravan(gold, numJugador, caravans)
        } else {
            if (gold[numJugador] > 5) {
                compraKnight(gold, numJugador, knights)
            }
            if (gold[numJugador] > 5) {
                compraKnight(gold, numJugador, knights)
            }


        }
    }

    static def estrategiaCompraBartol2(int turno, ArrayList<Integer> gold, ArrayList<Integer> caravans, ArrayList<Integer> knights) {
        int numJugador = 1
        if (turno == 1) {
            compraCaravan(gold, numJugador, caravans)
            compraCaravan(gold, numJugador, caravans)
        } else {
            MAX_KNIGHTS.times {
                if (gold[numJugador] > 5) {
                    compraKnight(gold, numJugador, knights)
                }
            }

            int posible = (gold[numJugador].intValue())/costCaravan
            posible = Math.min(posible, 3)
            println "Bartol puede comprar $posible caravanas"
            posible.times {
                compraCaravan(gold, numJugador, caravans)
            }
        }
    }

    static private void compraCaravan(ArrayList<Integer> gold, int numJugador, ArrayList<Integer> caravans) {
        gold[numJugador] = gold[numJugador] - costCaravan
        caravans[numJugador] = caravans[numJugador] + 1
    }

    static private void compraKnight(ArrayList<Integer> gold, int numJugador, ArrayList<Integer> knights) {
        gold[numJugador] = gold[numJugador] - costKnight
        knights[numJugador] = knights[numJugador] + 1
    }

    static def merchants(ArrayList<Integer> percMerchant, ArrayList<Integer> caravans, ArrayList<Integer> gold) {
        int num1 = Math.random()*100;
        int num2 = Math.random()*100;
        merchant(0, num1, percMerchant, caravans, gold)
        merchant(1, num2, percMerchant, caravans, gold)
    }

    static def merchant(int numJugador, int tirada, List<Integer> percMerchant, ArrayList<Integer> caravans, ArrayList<Integer> gold) {
        if (tirada<=percMerchant[numJugador]) {
            int bonoCaravanas = BENEFICIO_MERCHANT_EXITO*caravans[numJugador]
//            println "Comercio exitoso jug $numJugador !!"
            gold[numJugador] = gold[numJugador] + 5 + bonoCaravanas
        } else {
            int bonoCaravanas = BENEFICIO_MERCHANT_FALLO*caravans[numJugador]
            gold[numJugador] = gold[numJugador] + 5 + bonoCaravanas
        }
    }

    static def attacks(ArrayList<Integer> percAttack, ArrayList<Integer> caravans, ArrayList<Integer> knights, ArrayList<Integer> gold) {
        int ataques = knights[0]
//        println "$ataques ataques del jugador"
        ataques.times {
            int num1 = Math.random()*100;
            attackJugador(0, num1, percAttack, caravans, knights, gold)
        }
        ataques = knights[1]
//        println "$ataques ataques de Bartol"
        ataques.times {
            int num2 = Math.random()*100;
            attackJugador(1, num2, percAttack, caravans, knights, gold)
        }
    }

    static def attackJugador(int numJugador, int tirada, List<Integer> percAttack, ArrayList<Integer> caravans, ArrayList<Integer> knights, ArrayList<Integer> gold) {
        int otroJugador = numJugador == 0?1:0
        if (tirada <= percAttack[numJugador]) {
//            println "Ataque con éxito jug $numJugador!!!"
            gold[otroJugador] = gold[otroJugador] - 5
            if (caravans[otroJugador] > 0)
                caravans[otroJugador] = caravans[otroJugador] - 1
        } else {
            gold[otroJugador] = gold[otroJugador] - 1
        }
    }

    static def printPartida(ArrayList<Integer> gold, ArrayList<Integer> caravans, ArrayList<Integer> knights, String phase) {
        println "Phase $phase"
        println "  Oro $gold"
        println "  Caravans $caravans"
        println "  Knights $knights"
    }
}