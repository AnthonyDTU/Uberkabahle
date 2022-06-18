// package com.example.uberkabahle.src.test.java.src;

// import com.example.uberkabahle.src.main.java.src.Algorithm;
// import com.example.uberkabahle.src.main.java.src.Interfaces.Move;
// import com.example.uberkabahle.src.main.java.src.Interfaces.Table;
// import com.example.uberkabahle.src.main.java.src.Match;
// import com.example.uberkabahle.src.main.java.src.Mover;
// import com.example.uberkabahle.src.main.java.src.TableIO;

// import org.junit.Test;

// public class TestGames {

//     @Test
//     void testOne(){
//         Table table = new TableIO();
//         Algorithm algorithm = new Algorithm(table);
//         Move move = new Mover(table);
//         Match match;
//         table.initStartTable("S10,R9,S3,S5,H11,H4,H6");
//         match = algorithm.checkForAnyMatch();
//         match.nextPlayerCard = table.stringToCardConverter("S7");
//         move.moveCard_OrPile(match);

//         match = algorithm.checkForAnyMatch();
//         match.nextPlayerCard = table.stringToCardConverter("H10");
//         move.moveCard_OrPile(match);

//         match = algorithm.checkForAnyMatch();
//         match.nextPlayerCard = table.stringToCardConverter("K0");
//         move.moveCard_OrPile(match);

//         match = algorithm.checkForAnyMatch();
//         match.nextPlayerCard = table.stringToCardConverter("R9");
//         move.moveCard_OrPile(match);

//         System.out.printf("");
//     }

//     @Test
//     void testTwo(){
//         Table table = new TableIO();
//         Algorithm algorithm = new Algorithm(table);
//         Move move = new Mover(table);
//         Match match;
//         table.initStartTable("S10,R9,S3,S5,H11,H4,H6");
//         for (int i = 0 ; i < 7 ; i++) {
//             table.getAllPiles().get(i).clear();
//         }
//         table.getAllPiles().get(0).add(table.stringToCardConverter("S13"));
//         table.getAllPiles().get(0).add(table.stringToCardConverter("H12"));
//         table.getAllPiles().get(0).add(table.stringToCardConverter("K11"));
//         table.getAllPiles().get(0).add(table.stringToCardConverter("R10"));
//         table.getAllPiles().get(0).add(table.stringToCardConverter("S9"));
//         table.getAllPiles().get(0).add(table.stringToCardConverter("H8"));
//         table.getAllPiles().get(0).add(table.stringToCardConverter("K7"));
//         table.getAllPiles().get(0).add(table.stringToCardConverter("R6"));

//         table.getAllPiles().get(1).add(table.stringToCardConverter("H13"));
//         table.getAllPiles().get(1).add(table.stringToCardConverter("K12"));
//         table.getAllPiles().get(1).add(table.stringToCardConverter("R11"));
//         table.getAllPiles().get(1).add(table.stringToCardConverter("S10"));
//         table.getAllPiles().get(1).add(table.stringToCardConverter("H9"));
//         table.getAllPiles().get(1).add(table.stringToCardConverter("K8"));
//         table.getAllPiles().get(1).add(table.stringToCardConverter("R7"));

//         table.getAllPiles().get(2).add(table.stringToCardConverter("R13"));
//         table.getAllPiles().get(2).add(table.stringToCardConverter("S12"));
//         table.getAllPiles().get(2).add(table.stringToCardConverter("H11"));
//         table.getAllPiles().get(2).add(table.stringToCardConverter("K10"));
//         table.getAllPiles().get(2).add(table.stringToCardConverter("R9"));
//         table.getAllPiles().get(2).add(table.stringToCardConverter("S8"));

//         table.getAllPiles().get(3).add(table.stringToCardConverter("K13"));
//         table.getAllPiles().get(3).add(table.stringToCardConverter("R12"));
//         table.getAllPiles().get(3).add(table.stringToCardConverter("S11"));
//         table.getAllPiles().get(3).add(table.stringToCardConverter("H10"));
//         table.getAllPiles().get(3).add(table.stringToCardConverter("K9"));
//         table.getAllPiles().get(3).add(table.stringToCardConverter("R8"));
//         table.getAllPiles().get(3).add(table.stringToCardConverter("S7"));

//         table.getPlayerDeck_FaceUp().add(table.stringToCardConverter("S5"));
//         table.getPlayerDeck_FaceUp().add(table.stringToCardConverter("S6"));

//         match = algorithm.checkForAnyMatch();
//         move.moveCard_OrPile(match);
//         match = algorithm.checkForAnyMatch();
//         System.out.printf("");
//     }
// }
