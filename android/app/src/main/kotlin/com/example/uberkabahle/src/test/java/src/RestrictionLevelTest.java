// package com.example.uberkabahle.src.test.java.src;

// import com.example.uberkabahle.src.main.java.src.Algorithm;
// import com.example.uberkabahle.src.main.java.src.Interfaces.Move;
// import com.example.uberkabahle.src.main.java.src.Interfaces.RestrictionLevel;
// import com.example.uberkabahle.src.main.java.src.Interfaces.Table;
// import com.example.uberkabahle.src.main.java.src.Match;
// import com.example.uberkabahle.src.main.java.src.Mover;
// import com.example.uberkabahle.src.main.java.src.TableIO;

// import org.junit.jupiter.api.Test;
// import static org.testng.Assert.*;



// public class RestrictionLevelTest {

//     @Test
//     void doesRestrictionStateChanges(){

//     //Assert that the algorithm moves the value two to tablou in case of ease of restriction level.
//         Table table = new TableIO();
//         Algorithm algorithm = new Algorithm(table);
//         Move move = new Mover(table);
//         Match match;
//         table.initStartTable("H13,H13,K3,R13,R13,H13,R13");
//         for (int i = 0 ; i < 7 ; i++) {
//             match = algorithm.checkForAnyMatch();
//             match.nextPlayerCard = table.stringToCardConverter("K13");
//             move.moveCard_OrPile(match);
//         }
//         assertEquals(algorithm.getRestrictionState(), RestrictionLevel.HIGH, "Check that the restriction level is high");
//         System.out.println("");
//         match = algorithm.checkForAnyMatch();
//         assertEquals(algorithm.getRestrictionState(), RestrictionLevel.LOW, "Check that the restriction level is low");
//         match.nextPlayerCard = table.stringToCardConverter("H2");
//         move.moveCard_OrPile(match);
//         match = algorithm.checkForAnyMatch();
//         assertTrue(match.isMatch());
//         match.nextPlayerCard = table.stringToCardConverter("K13");
//         move.moveCard_OrPile(match);
//         match = algorithm.checkForAnyMatch();
//         System.out.println("");
//         assertEquals(algorithm.getRestrictionState(), RestrictionLevel.HIGH, "Check that the restriction level is high, after modulus 3 is no longer 0");

//     }

// }
