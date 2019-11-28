/***********************************************************************************

A 2d array implementation of the glyphs of code page 437.


***********************************************************************************/

package WidlerSuite;

public interface CP437
{
   public static final char[][] CP437_TABLE = {
      {' ', (char)9658,  ' ',  '0',  '@',  'P',  '`',  'p',  
         (char)199,  (char)201,  (char)225,  (char)9617,  (char)9492,  (char)9576,  (char)945,  (char)8801},
      {(char)9786,  (char)9668, '!', '1', 'A', 'Q', 'a', 'q', 
         (char)252, (char)230, (char)237, (char)9618, (char)9524, (char)9572, (char)223, (char)177},
      {(char)9787,  (char)8597, '"', '2', 'B', 'R', 'b', 'r', 
         (char)233, (char)198, (char)243, (char)9619, (char)9516, (char)9573, (char)915, (char)8805},
      {(char)9829,  (char)8252, '#', '3', 'C', 'S', 'c', 's', 
         (char)226, (char)244, (char)250, (char)9474, (char)9500, (char)9561, (char)960, (char)8804},
      
      {(char)9830, (char)182, '$', '4', 'D', 'T', 'd', 't', 
         (char)228, (char)246, (char)241, (char)9508, (char)9472, (char)9560, (char)931, (char)8992},
      {(char)9827,  (char)167, '%', '5', 'E', 'U', 'e', 'u',
         (char)224, (char)242, (char)209, (char)9569, (char)9532, (char)9554, (char)963, (char)8993},
      {(char)9824,  (char)9644, '&', '6', 'F', 'V', 'f', 'v', 
         (char)229, (char)251, (char)170, (char)9570, (char)9566, (char)9555, (char)181, (char)247},
      {(char)8226,  (char)8616, '\'', '7', 'G', 'W', 'g', 'w', 
         (char)231, (char)249, (char)186, (char)9558, (char)9567, (char)9579, (char)964, (char)8776},
      
      {(char)9688,  (char)8593, '(', '8', 'H', 'X', 'h', 'x', 
         (char)234, (char)255, (char)191, (char)9557, (char)9562, (char)9578, (char)934, (char)176},
      {(char)9675,  (char)8595, ')', '9', 'I', 'Y', 'i', 'y',
         (char)235, (char)214, (char)8976, (char)9571, (char)9556, (char)9496, (char)920, (char)8729},
      {(char)9689,  (char)8594, '*', ':', 'J', 'Z', 'j', 'z',
         (char)232, (char)220, (char)172, (char)9553, (char)9577, (char)9484, (char)937, (char)183},
      {(char)9794,  (char)8592, '+', ';', 'K', '[', 'k', '{',
         (char)239, (char)162, (char)189, (char)9559, (char)9574, (char)9608, (char)948, (char)8730},
      
      {(char)9792,  (char)8735, ',', '<', 'L', '\\', 'l', '|',
         (char)238, (char)163, (char)188, (char)9565, (char)9568, (char)9604, (char)8734, (char)8319},
      {(char)9834,  (char)8596, '-', '=', 'M', ']', 'm', '}',
         (char)236, (char)165, (char)161, (char)9564, (char)9552, (char)9612, (char)966, (char)178},
      {(char)9835,  (char)9650, '.', '>', 'N', '^', 'n', '~',
         (char)196, (char)8359, (char)171, (char)9563, (char)9580, (char)9616, (char)949, (char)9632},
      {(char)9788,  (char)9660, '/', '?', 'O', '_', 'o', (char)8962, 
         (char)197, (char)402, (char)187, (char)9488, (char)9575, (char)9600, (char)8745, ' '}
   };
   
   public static final char NULL_CHAR = CP437_TABLE[0x0][0x0];
   public static final char SMILE_NO_FILL_CHAR = CP437_TABLE[0x1][0x0];
   public static final char SMILE_FILL_CHAR = CP437_TABLE[0x2][0x0];
   public static final char HEART_CHAR = CP437_TABLE[0x3][0x0];
   public static final char DIAMOND_CHAR = CP437_TABLE[0x4][0x0];
   public static final char CLUB_CHAR = CP437_TABLE[0x5][0x0];
   public static final char SPADE_CHAR = CP437_TABLE[0x6][0x0];
   public static final char DOT_FILL_CHAR = CP437_TABLE[0x7][0x0];
   public static final char DOT_FILL_COMPLEMENT_CHAR = CP437_TABLE[0x8][0x0];
   public static final char DOT_NO_FILL_CHAR = CP437_TABLE[0x9][0x0];
   public static final char DOT_NO_FILL_COMPLEMENT_CHAR = CP437_TABLE[0xA][0x0];
   public static final char MALE_CHAR = CP437_TABLE[0xB][0x0];
   public static final char FEMALE_CHAR = CP437_TABLE[0xC][0x0];
   public static final char NOTE_CHAR = CP437_TABLE[0xD][0x0];
   public static final char DOUBLE_NOTE_CHAR = CP437_TABLE[0xE][0x0];
   public static final char SUN_CHAR = CP437_TABLE[0xF][0x0];
   
   public static final char RIGHT_TRIANGLE_CHAR = CP437_TABLE[0x0][0x1];
   public static final char LEFT_TRIANGLE_CHAR = CP437_TABLE[0x1][0x1];
   public static final char DOUBLE_VERTICAL_ARROW_CHAR = CP437_TABLE[0x2][0x1];
   public static final char DOUBLE_EXCLAMATION_CHAR = CP437_TABLE[0x3][0x1];
   public static final char PARAGRAPH_CHAR = CP437_TABLE[0x4][0x1];
   public static final char DOUBLE_S_CHAR = CP437_TABLE[0x5][0x1];
   public static final char SMALL_BOX_CHAR = CP437_TABLE[0x6][0x1];
 //  public static final char UNDERLINE_DOUBLE_VERTICAL_ARROW_CHAR = CP437_TABLE[0x7][0x1];
   public static final char UP_ARROW_CHAR = CP437_TABLE[0x8][0x1];
   public static final char DOWN_ARROW_CHAR = CP437_TABLE[0x9][0x1];
   public static final char RIGHT_ARROW_CHAR = CP437_TABLE[0xA][0x1];
   public static final char LEFT_ARROW_CHAR = CP437_TABLE[0xB][0x1];
 //  public static final char _CHAR = CP437_TABLE[0xC][0x1];
   public static final char DOUBLE_HORIZONTAL_CHAR = CP437_TABLE[0xD][0x1];
   public static final char UP_TRIANGLE_CHAR = CP437_TABLE[0xE][0x1];
   public static final char DOWN_TRIANGLE_CHAR = CP437_TABLE[0xF][0x1];
   /*
   public static final char _CHAR = CP437_TABLE[0x0][0x2];
   public static final char _CHAR = CP437_TABLE[0x1][0x2];
   public static final char _CHAR = CP437_TABLE[0x2][0x2];
   public static final char _CHAR = CP437_TABLE[0x3][0x2];
   public static final char _CHAR = CP437_TABLE[0x4][0x2];
   public static final char _CHAR = CP437_TABLE[0x5][0x2];
   public static final char _CHAR = CP437_TABLE[0x6][0x2];
   public static final char _CHAR = CP437_TABLE[0x7][0x2];
   public static final char _CHAR = CP437_TABLE[0x8][0x2];
   public static final char _CHAR = CP437_TABLE[0x9][0x2];
   public static final char _CHAR = CP437_TABLE[0xA][0x2];
   public static final char _CHAR = CP437_TABLE[0xB][0x2];
   public static final char _CHAR = CP437_TABLE[0xC][0x2];
   public static final char _CHAR = CP437_TABLE[0xD][0x2];
   public static final char _CHAR = CP437_TABLE[0xE][0x2];
   public static final char _CHAR = CP437_TABLE[0xF][0x2];
   
   public static final char _CHAR = CP437_TABLE[0x0][0x3];
   public static final char _CHAR = CP437_TABLE[0x1][0x3];
   public static final char _CHAR = CP437_TABLE[0x2][0x3];
   public static final char _CHAR = CP437_TABLE[0x3][0x3];
   public static final char _CHAR = CP437_TABLE[0x4][0x3];
   public static final char _CHAR = CP437_TABLE[0x5][0x3];
   public static final char _CHAR = CP437_TABLE[0x6][0x3];
   public static final char _CHAR = CP437_TABLE[0x7][0x3];
   public static final char _CHAR = CP437_TABLE[0x8][0x3];
   public static final char _CHAR = CP437_TABLE[0x9][0x3];
   public static final char _CHAR = CP437_TABLE[0xA][0x3];
   public static final char _CHAR = CP437_TABLE[0xB][0x3];
   public static final char _CHAR = CP437_TABLE[0xC][0x3];
   public static final char _CHAR = CP437_TABLE[0xD][0x3];
   public static final char _CHAR = CP437_TABLE[0xE][0x3];
   public static final char _CHAR = CP437_TABLE[0xF][0x3];
   
   public static final char _CHAR = CP437_TABLE[0x0][0x4];
   public static final char _CHAR = CP437_TABLE[0x1][0x4];
   public static final char _CHAR = CP437_TABLE[0x2][0x4];
   public static final char _CHAR = CP437_TABLE[0x3][0x4];
   public static final char _CHAR = CP437_TABLE[0x4][0x4];
   public static final char _CHAR = CP437_TABLE[0x5][0x4];
   public static final char _CHAR = CP437_TABLE[0x6][0x4];
   public static final char _CHAR = CP437_TABLE[0x7][0x4];
   public static final char _CHAR = CP437_TABLE[0x8][0x4];
   public static final char _CHAR = CP437_TABLE[0x9][0x4];
   public static final char _CHAR = CP437_TABLE[0xA][0x4];
   public static final char _CHAR = CP437_TABLE[0xB][0x4];
   public static final char _CHAR = CP437_TABLE[0xC][0x4];
   public static final char _CHAR = CP437_TABLE[0xD][0x4];
   public static final char _CHAR = CP437_TABLE[0xE][0x4];
   public static final char _CHAR = CP437_TABLE[0xF][0x4];
   
   public static final char _CHAR = CP437_TABLE[0x0][0x5];
   public static final char _CHAR = CP437_TABLE[0x1][0x5];
   public static final char _CHAR = CP437_TABLE[0x2][0x5];
   public static final char _CHAR = CP437_TABLE[0x3][0x5];
   public static final char _CHAR = CP437_TABLE[0x4][0x5];
   public static final char _CHAR = CP437_TABLE[0x5][0x5];
   public static final char _CHAR = CP437_TABLE[0x6][0x5];
   public static final char _CHAR = CP437_TABLE[0x7][0x5];
   public static final char _CHAR = CP437_TABLE[0x8][0x5];
   public static final char _CHAR = CP437_TABLE[0x9][0x5];
   public static final char _CHAR = CP437_TABLE[0xA][0x5];
   public static final char _CHAR = CP437_TABLE[0xB][0x5];
   public static final char _CHAR = CP437_TABLE[0xC][0x5];
   public static final char _CHAR = CP437_TABLE[0xD][0x5];
   public static final char _CHAR = CP437_TABLE[0xE][0x5];
   public static final char _CHAR = CP437_TABLE[0xF][0x5];
   
   public static final char _CHAR = CP437_TABLE[0x0][0x6];
   public static final char _CHAR = CP437_TABLE[0x1][0x6];
   public static final char _CHAR = CP437_TABLE[0x2][0x6];
   public static final char _CHAR = CP437_TABLE[0x3][0x6];
   public static final char _CHAR = CP437_TABLE[0x4][0x6];
   public static final char _CHAR = CP437_TABLE[0x5][0x6];
   public static final char _CHAR = CP437_TABLE[0x6][0x6];
   public static final char _CHAR = CP437_TABLE[0x7][0x6];
   public static final char _CHAR = CP437_TABLE[0x8][0x6];
   public static final char _CHAR = CP437_TABLE[0x9][0x6];
   public static final char _CHAR = CP437_TABLE[0xA][0x6];
   public static final char _CHAR = CP437_TABLE[0xB][0x6];
   public static final char _CHAR = CP437_TABLE[0xC][0x6];
   public static final char _CHAR = CP437_TABLE[0xD][0x6];
   public static final char _CHAR = CP437_TABLE[0xE][0x6];
   public static final char _CHAR = CP437_TABLE[0xF][0x6];
   
   public static final char _CHAR = CP437_TABLE[0x0][0x7];
   public static final char _CHAR = CP437_TABLE[0x1][0x7];
   public static final char _CHAR = CP437_TABLE[0x2][0x7];
   public static final char _CHAR = CP437_TABLE[0x3][0x7];
   public static final char _CHAR = CP437_TABLE[0x4][0x7];
   public static final char _CHAR = CP437_TABLE[0x5][0x7];
   public static final char _CHAR = CP437_TABLE[0x6][0x7];
   public static final char _CHAR = CP437_TABLE[0x7][0x7];
   public static final char _CHAR = CP437_TABLE[0x8][0x7];
   public static final char _CHAR = CP437_TABLE[0x9][0x7];
   public static final char _CHAR = CP437_TABLE[0xA][0x7];
   public static final char _CHAR = CP437_TABLE[0xB][0x7];
   public static final char _CHAR = CP437_TABLE[0xC][0x7];
   public static final char _CHAR = CP437_TABLE[0xD][0x7];
   public static final char _CHAR = CP437_TABLE[0xE][0x7];*/
   public static final char DELETE_CHAR = CP437_TABLE[0xF][0x7];
   /*
   public static final char _CHAR = CP437_TABLE[0x0][0x8];
   public static final char _CHAR = CP437_TABLE[0x1][0x8];
   public static final char _CHAR = CP437_TABLE[0x2][0x8];
   public static final char _CHAR = CP437_TABLE[0x3][0x8];
   public static final char _CHAR = CP437_TABLE[0x4][0x8];
   public static final char _CHAR = CP437_TABLE[0x5][0x8];
   public static final char _CHAR = CP437_TABLE[0x6][0x8];
   public static final char _CHAR = CP437_TABLE[0x7][0x8];
   public static final char _CHAR = CP437_TABLE[0x8][0x8];
   public static final char _CHAR = CP437_TABLE[0x9][0x8];
   public static final char _CHAR = CP437_TABLE[0xA][0x8];
   public static final char _CHAR = CP437_TABLE[0xB][0x8];
   public static final char _CHAR = CP437_TABLE[0xC][0x8];
   public static final char _CHAR = CP437_TABLE[0xD][0x8];
   public static final char _CHAR = CP437_TABLE[0xE][0x8];
   public static final char _CHAR = CP437_TABLE[0xF][0x8];
   
   public static final char _CHAR = CP437_TABLE[0x0][0x9];
   public static final char _CHAR = CP437_TABLE[0x1][0x9];
   public static final char _CHAR = CP437_TABLE[0x2][0x9];
   public static final char _CHAR = CP437_TABLE[0x3][0x9];
   public static final char _CHAR = CP437_TABLE[0x4][0x9];
   public static final char _CHAR = CP437_TABLE[0x5][0x9];
   public static final char _CHAR = CP437_TABLE[0x6][0x9];
   public static final char _CHAR = CP437_TABLE[0x7][0x9];
   public static final char _CHAR = CP437_TABLE[0x8][0x9];
   public static final char _CHAR = CP437_TABLE[0x9][0x9];
   public static final char _CHAR = CP437_TABLE[0xA][0x9];*/
   public static final char CENT_CHAR = CP437_TABLE[0xB][0x9];
   public static final char GBP_CHAR = CP437_TABLE[0xC][0x9];
   public static final char YEN_CHAR = CP437_TABLE[0xD][0x9];
   public static final char PTS_CHAR = CP437_TABLE[0xE][0x9];
   public static final char FUNCTION_CHAR = CP437_TABLE[0xF][0x9];
   
  /* public static final char _CHAR = CP437_TABLE[0x0][0xA];
   public static final char _CHAR = CP437_TABLE[0x1][0xA];
   public static final char _CHAR = CP437_TABLE[0x2][0xA];
   public static final char _CHAR = CP437_TABLE[0x3][0xA];
   public static final char _CHAR = CP437_TABLE[0x4][0xA];
   public static final char _CHAR = CP437_TABLE[0x5][0xA];
   public static final char _CHAR = CP437_TABLE[0x6][0xA];
   public static final char _CHAR = CP437_TABLE[0x7][0xA];*/
   public static final char INVERTED_QUESTION_MARK_CHAR = CP437_TABLE[0x8][0xA];
 //  public static final char _CHAR = CP437_TABLE[0x9][0xA];
 //  public static final char _CHAR = CP437_TABLE[0xA][0xA];
   public static final char ONE_HALF_CHAR = CP437_TABLE[0xB][0xA];
   public static final char ONE_QUARTER_CHAR = CP437_TABLE[0xC][0xA];
   public static final char INVERTED_EXCLAMATION_MARK_CHAR = CP437_TABLE[0xD][0xA];
   public static final char DOUBLE_LEFT_ARROW_CHAR = CP437_TABLE[0xE][0xA];
   public static final char DOUBLE_RIGHT_ARROW_CHAR = CP437_TABLE[0xF][0xA];
   
   public static final char LIGHT_SHADE_CHAR = CP437_TABLE[0x0][0xB];
   public static final char MEDIUM_SHADE_CHAR = CP437_TABLE[0x1][0xB];
   public static final char DARK_SHADE_CHAR = CP437_TABLE[0x2][0xB];/*
   public static final char BOX_n0s0_CHAR = CP437_TABLE[0x3][0xB];
   public static final char BOX_n0sw_CHAR = CP437_TABLE[0x4][0xB];
   public static final char BOX_n0sW_CHAR = CP437_TABLE[0x5][0xB];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x6][0xB];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x7][0xB];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x8][0xB];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x9][0xB];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xA][0xB];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xB][0xB];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xC][0xB];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xD][0xB];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xE][0xB];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xF][0xB];
   
   public static final char BOX_0000_CHAR = CP437_TABLE[0x0][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x1][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x2][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x3][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x4][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x5][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x6][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x7][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x8][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x9][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xA][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xB][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xC][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xD][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xE][0xC];
   public static final char BOX_0000_CHAR = CP437_TABLE[0xF][0xC];
   
   public static final char BOX_0000_CHAR = CP437_TABLE[0x0][0xD];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x1][0xD];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x2][0xD];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x3][0xD];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x4][0xD];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x5][0xD];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x6][0xD];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x7][0xD];
   public static final char BOX_0000_CHAR = CP437_TABLE[0x8][0xD];
   public static final char _CHAR = CP437_TABLE[0x9][0xD];
   public static final char _CHAR = CP437_TABLE[0xA][0xD];*/
   public static final char FULL_BLOCK_CHAR = CP437_TABLE[0xB][0xD];
   public static final char LOWER_HALF_BLOCK_CHAR = CP437_TABLE[0xC][0xD];
   public static final char LEFT_HALF_BLOCK_CHAR = CP437_TABLE[0xD][0xD];
   public static final char RIGHT_HALF_BLOCK_CHAR = CP437_TABLE[0xE][0xD];
   public static final char UPPER_HALF_BLOCK_CHAR = CP437_TABLE[0xF][0xD];
   
   public static final char ALPHA_CHAR = CP437_TABLE[0x0][0xE];
   public static final char BETA_CHAR = CP437_TABLE[0x1][0xE];
   public static final char GAMMA_CHAR = CP437_TABLE[0x2][0xE];
   public static final char PI_CHAR = CP437_TABLE[0x3][0xE];
   public static final char CAPITAL_SIGMA_CHAR = CP437_TABLE[0x4][0xE];
   public static final char SMALL_SIGMA_CHAR = CP437_TABLE[0x5][0xE];
   public static final char MU_CHAR = CP437_TABLE[0x6][0xE];
   public static final char TAU_CHAR = CP437_TABLE[0x7][0xE];
   public static final char CAPITAL_PHI_CHAR = CP437_TABLE[0x8][0xE];
   public static final char THETA_CHAR = CP437_TABLE[0x9][0xE];
   public static final char OMEGA_CHAR = CP437_TABLE[0xA][0xE];
   public static final char DELTA_CHAR = CP437_TABLE[0xB][0xE];
   public static final char INFINITY_CHAR = CP437_TABLE[0xC][0xE];
   public static final char SMALL_PHI_CHAR = CP437_TABLE[0xD][0xE];
   public static final char EPSILON_CHAR = CP437_TABLE[0xE][0xE];
   public static final char INTERSECTION_CHAR = CP437_TABLE[0xF][0xE];
   
   public static final char TRIPLE_BAR_CHAR = CP437_TABLE[0x0][0xF];
   public static final char PLUS_MINUS_CHAR = CP437_TABLE[0x1][0xF];
   public static final char GTE_CHAR = CP437_TABLE[0x2][0xF];
   public static final char LTE_CHAR = CP437_TABLE[0x3][0xF];
   public static final char INTEGRAL_TOP_CHAR = CP437_TABLE[0x4][0xF];
   public static final char INTEGRAL_BOTTOM_CHAR = CP437_TABLE[0x5][0xF];
   public static final char DIVISION_CHAR = CP437_TABLE[0x6][0xF];
   public static final char ALMOST_EQUAL_CHAR = CP437_TABLE[0x7][0xF];
   public static final char DEGREE_CHAR = CP437_TABLE[0x8][0xF];
   public static final char BULLET_CHAR = CP437_TABLE[0x9][0xF];
   public static final char MIDDLE_DOT_CHAR = CP437_TABLE[0xA][0xF];
   public static final char SQRT_CHAR = CP437_TABLE[0xB][0xF];
   public static final char SUPERSCRIPT_N_CHAR = CP437_TABLE[0xC][0xF];
   public static final char SUPERSCRIPT_2_CHAR = CP437_TABLE[0xD][0xF];
   public static final char BLACK_SQUARE_CHAR = CP437_TABLE[0xE][0xF];
   public static final char EMPTY_CHAR = CP437_TABLE[0xF][0xF];
}