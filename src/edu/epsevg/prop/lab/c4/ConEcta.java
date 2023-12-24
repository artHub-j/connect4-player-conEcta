package edu.epsevg.prop.lab.c4;

import java.lang.*;

/**
 * Jugador ConEcta
 * @author Arturo Aragón Hidalgo
 * @author Nawal Bouallala Safyoun
 */
public class ConEcta implements Jugador, IAuto 
{
    // Variables globals.
    private int INFINIT = Integer.MAX_VALUE; // ∞
    private int MENYS_INFINIT = Integer.MIN_VALUE; // -∞

    // Atributs de la classe.
    private String _nom;
    private int _taulersExplorats, _taulersPartida;
    private int _profunditatInicial = 8; // Per defecte es 8.
    private Boolean _ambPoda;
    
    private int _numJugada = 0;
    
    // Matriu de pesos inicial.
    private int[][] _matriuPesos = {{3, 4,  5,  7,  7,  5, 4, 3}, 
                                    {4, 6,  8, 11, 11,  8, 6, 4}, 
                                    {5, 8, 11, 13, 13, 11, 8, 5}, 
                                    {5, 8, 11, 13, 13, 11, 8, 5}, 
                                    {5, 8, 11, 13, 13, 11, 8, 5}, 
                                    {5, 8, 11, 13, 13, 11, 8, 5}, 
                                    {4, 6,  8,  7,  7,  8, 6, 4},
                                    {3, 4,  5,  7,  7,  5, 4, 3}};

    /**
     * Constructor de Jugador ConEcta. Busca jugades fins la profunditat prof.
     * @param prof Profunditat maxima fins la que es cercarà a l'arbre de jugades.             
     * @param ambPoda Indiquem si volem fer minimax amb/sense poda alpha-beta. 
    */
    public ConEcta(int prof, boolean ambPoda) throws Exception 
    {
      _nom = "ConEcta";
      _taulersExplorats = 0;
      _taulersPartida = 0;
      _ambPoda = ambPoda;
      _profunditatInicial = prof;
    } 
    
    /***
     * Funció que retorna el nom del jugador.
     * @return Retorna el nom del jugador que s'utlilitza per visualització a la UI.
     */
    @Override
    public String nom() //Funcio que retorna el nom
    {
      return _nom;
    }
 
    /***
     * Funció que retorna el temps trigat per cada tirada, els taulers explorats de cada tirada i els taulers explorats durant tota la partida.
     * @param t Estat acual del Tauler de la partida.
     * @param color Color de la fitxa del jugador que jugarà (-1, 0, 1).
     * @return Retorna la millor tirada segons el color del jugador que jugarà.
     */
    @Override
    public int moviment(Tauler t, int color) {
        ++_numJugada;
        _taulersExplorats = 0;
        long tempsInicial = System.currentTimeMillis();
        int alpha = MENYS_INFINIT;
        int beta = INFINIT;
        int actual = MENYS_INFINIT, best_column = -1;
        for (int mov = 0; mov < t.getMida(); ++mov){
            if (t.movpossible(mov)) {
                Tauler taux = new Tauler(t);
                taux.afegeix(mov, color);
                int valor_min = min(taux, mov, _profunditatInicial - 1, color, alpha, beta);
                if (actual <= valor_min) {  //Si trobem un millor valor, actualitzem actual
                    actual = valor_min;
                    best_column = mov;
                }
            }
        }
        long tempsFinal = System.currentTimeMillis();
        double temps = (tempsFinal - tempsInicial) / 1000.0;    //calcular el temps 
        System.out.println("Jugada [" + _numJugada + "] amb Temps: " + temps + " s");
        System.out.println(" > Columna Final Escollida:" + best_column);
        System.out.println(" > TAULERS EXAMINATS PER MOVIMENT: " + _taulersExplorats);
        System.out.println(" > TAULERS EXPLORATS TOTALS A LA PARTIDA: " + _taulersPartida);
        return best_column; //retorna la millor columna
    }
    
    /***
     * Funció per calcular la tirada amb l'heuristica maxima de totes les posibles tirades, i també té en compte si es amb poda o sense
     * @param t Estat acual del Tauler de la partida.
     * @param columna Conté la columna que estem tractant.
     * @param profundidad valor maxim de la profunditat de l'arbre de possibles estats de la partida.
     * @param colactual Color de la fitxa del jugador que jugarà (-1, 0, 1).
     * @param alpha rang superior per la poda alpha-beta.
     * @param beta rang inferior per la poda alpha-beta.
     * @return Retorna el màxim de l'algorisme minimax.
     */
    private int max(Tauler t, int columna, int profundidad, int colactual, int alpha, int beta){
        int best_valor = MENYS_INFINIT;
        ++_taulersExplorats;
        ++_taulersPartida;
        int c = obtenerUltimoColor(t, columna); 
        if (t.solucio(columna, c)) {
            return best_valor;
        }
        else if (profundidad == 0 || !t.espotmoure()) {
            return heuristicaFitxesConsec(t, colactual);
        }
        else {
            for (int mov = 0; mov < t.getMida(); ++mov) {
                if (t.movpossible(mov)) {
                    Tauler taux = new Tauler(t);
                    taux.afegeix(mov, colactual);
                    int actual = min(taux, mov, profundidad - 1, colactual, alpha, beta);
                    best_valor = Math.max(best_valor, actual); // Escollir el maxim dels minims
                    if (_ambPoda) {
                        alpha = Math.max(best_valor, alpha);
                        if (beta <= alpha) { // PODA
                            break;
                        }
                    }
                }
            }   
        }
        return best_valor;
    }
    
    /***
     * Funcio per calcular la tirada amb l'heuristica minima de totes les posibles tirades.
     * Segons el valor de _ambPoda farà minimax amb/sense poda alpha-beta.
     * @param t Estat acual del Tauler de la partida.
     * @param columna Conté la columna que estem tractant.
     * @param profundidad valor maxim de la profunditat de l'arbre de possibles estats de la partida.
     * @param colactual Color de la fitxa del jugador que jugarà (-1, 0, 1).
     * @param alpha Rang superior per la poda alpha-beta.
     * @param beta Rang inferior per la poda alpha-beta.
     * @return Retorna el mínim de l'algorisme minimax.
     */
    private int min(Tauler t, int columna, int profundidad, int colactual, int alpha, int beta) {
        int best_valor = INFINIT;
        ++_taulersExplorats;
        ++_taulersPartida;
        int c = obtenerUltimoColor(t, columna);
        if (t.solucio(columna, c)) {
            return best_valor;
        }
        else if (profundidad==0 || !t.espotmoure()) {
            return heuristicaFitxesConsec(t, colactual);
        }
        else {
            for (int mov = 0; mov < t.getMida(); mov++){
                if(t.movpossible(mov)){
                    Tauler taux = new Tauler(t);
                    taux.afegeix(mov, -colactual);
                    int actual = max(taux, mov, profundidad-1, colactual, alpha, beta);
                    best_valor = Math.min(best_valor, actual); //escollir el minim dels maxims
                    if(_ambPoda){
                        beta = Math.min(best_valor, beta); 
                        if(beta <= alpha){ // PODA
                            break;
                        }
                    }
                }
            }   
        }
        return best_valor;
    }

    /***
     * Funció auxiliar per obtenir l'últim color en un columna .
     * @param t Estat acual del Tauler de la partida.
     * @param columna columna sobre la que farem la cerca.
     * @return Retorna el color de la última fitxa col.locada en una columna.
     */
    private int obtenerUltimoColor(Tauler t, int columna){
        for (int i = t.getMida() - 1; i >= 0; --i) {
            int c = t.getColor(i, columna);
            if (c != 0) return c;
        }
        return 0; // Si no es troba cap color, tornar un 0 (casella buida).
    }
 
    /***
     * Heuristica amb una matriu amb diferents pesos per escollir columnes.
     * @param t Estat acual del Tauler de la partida.
     * @param color Color de la fitxa del jugador que jugarà (-1, 0, 1).
     * @return retorna la columna de la millor tirada, segons la matriu de Pesos.
     */
    public int heuristica1(Tauler t, int color) 
    {
        _taulersExplorats += 1; // Comptem quantes vegades hem calulat l'heuristica.
        Tauler ct = new Tauler(t);
        int max = _matriuPesos[0][0];
        Boolean millorTiradaTrobada = false;
        int r = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                millorTiradaTrobada = (ct.getColor(i, j) == 0 && _matriuPesos[i][j] > max);
                if (millorTiradaTrobada) {
                  max = _matriuPesos[i][j];
                  r = j;
                }
            }
        }

      return r;
    }
  
   /***
   * Funcio de l'heuristica que avalua totes opcions possibles, segons el nombre de fitxes consecutives
   * @param t Estat acual del Tauler de la partida.
   * @param color Color de la fitxa del jugador que jugarà (-1, 0, 1).
   * @return retorna la suma de totes les heuristicas calculades de totes les direccions
   */
    public int heuristicaFitxesConsec(Tauler t, int color) {
        int h = 0; 
        for (int c = 0; c < t.getMida(); ++c) {
            for (int f = 0; f < t.getMida(); ++f) {
                int col = t.getColor(f,c);
                int signe = 1;  
                if(col == 0) {
                    f = t.getMida();
                } else { 
                    if (col == -color) signe = -1;
                     // HORIZONTAL
                    if (!((c - 1 >= 0 && t.getColor(f, c - 1) == color) || (c + 3 >= t.getMida()))) {
                        h += evalua(t, col, c, f, 1, 0, "hdreta")*signe;
                    }
                    if (!((c + 1 < t.getMida() && t.getColor(f, c + 1) == color) || (c - 3 < 0)) && t.getColor(f, c - 3) == 0) {
                        h += evalua(t, col, c, f, -1, 0,"hesq")*signe;
                    }

                    // VERTICAL
                    if (!((f - 1 >= 0 && t.getColor(f - 1, c) == color) || (f + 3 >= t.getMida()))) {
                        h += evalua(t, col, c, f, 0, 1, "columna") * signe;
                    }
                    /*if  (!((f+1<t.getMida() && t.getColor(f+1, c)==color))){
                        h += evalua(t, col, c, f, 0, -1,"columnad")*signe;}*/

                    // DIAGONALS
                    if (f < t.getMida() - 3 && c < t.getMida() - 3) { 
                        // Diagonal adalt dreta
                        if (!((c - 1 >= 0 && f - 1 >= 0 && t.getColor(f - 1, c - 1) == color) || (c + 3 >= t.getMida() || f + 3 >= t.getMida()))) {
                            h += evalua(t, col, c, f, 1, 1, "upxy") * signe;
                        }
                        // Diagonal abaix esquerra
                        if (!((c + 1 < t.getMida() && f + 1 < t.getMida() && t.getColor(f + 1, c + 1) == color) || (c - 3 < 0 || f - 3 < 0)) && 
                           (t.getColor(f - 3, c - 3) == 0)) {
                            h += evalua(t, col, c, f, -1, -1, "dxy") * signe;
                        }
                        // Diagonal adalt esquerra
                        if (!((c + 1 < t.getMida() && f - 1 >= 0 && t.getColor(f - 1, c + 1) == color) || (c - 3 < 0 || f + 3 >= t.getMida()))) {
                            h += evalua(t, col, c, f, -1, 1, "uydx") * signe;
                        }
                        // Diagonal abaix dreta
                       if (!((c - 1 >= 0 && f + 1 < t.getMida() && t.getColor(f + 1, c - 1) == color) || (c + 3 >= t.getMida() || f - 3 < 0)) && 
                           t.getColor(f - 3, c + 3) == 0) {
                            h += evalua(t, col, c, f, 1, -1, "uxdy") * signe;
                       }
                    }
                
                }
            }
        }
        return h;
    }
    
    /***
     * Funció que calcula l'heurítica segons el nombre de fitxes consecutives o possibles(color==0)
     * @param t Estat acual del Tauler de la partida.
     * @param color Color de la fitxa del jugador que jugarà (-1, 0, 1).
     * @param col Conté la columna que estem tractant.
     * @param fil Conté la fila que estem tractant.
     * @param dirCol Conté la direcció de la columna
     * @param dirFil Conté la direcció de la fila
     * @param dir Es un string per indicar en quina direccio estem calculant, i serveix per el cas especial de les columnes
     * @return Retorna l'heurítica segons el nombre de fitxes consecutives o possibles
     */
    public int evalua(Tauler t, int color, int col, int fil, int dirCol, int dirFil, String dir) {
        int h = 0;
        int consec = 0;
        int pos = 1;
        for (int i = 0; i < 4; i++) {
            int newRow = fil + pos * dirFil;
            int newCol = col + pos * dirCol;
            if (dir == "columna") {
                newRow = fil + consec+1;
            }
            if (newRow >= 0 && newRow < t.getMida() && newCol >= 0 && newCol < t.getMida()) {
                int c = t.getColor(newRow, newCol);

                if (c == color) {
                    consec++;
                    if (dir != "columna") {
                        pos++;
                    }
                    if (consec == 3) {
                        h = 100;
                        break;
                    }
                    if (dir != "columna"){
                        if(pos == 4){
                            if(consec == 0) h += 1;
                            else if (consec == 1) h += 5;
                            else if (consec == 2) h += 10;
                            break;
                        } 
                    }
                } else if (c == 0) {
                    if (dir != "columna") pos++;
                    
                    if (dir != "columna") {
                        if(pos == 4) {
                            if (consec == 0) h += 1;
                            else if (consec == 1) h += 5;
                            else if (consec == 2) h += 10;
                            break;
                        } 
                    } else {
                        if (consec == 0) h += 1;
                        else if(consec == 1) h += 5;
                        else if(consec == 2) h += 10;
                        break;
                    }
                } else {
                    break; // Se encontró una ficha del oponente, no seguir en esta dirección
                }
            }
       }
        return h;
    }
}