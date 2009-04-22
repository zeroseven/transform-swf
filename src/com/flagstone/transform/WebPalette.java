/*
 * ColorTable.java
 * Transform
 * 
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES, LOSS OF USE, 
 * DATA, OR PROFITS, OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform;

/**
 * //TODO(code) Refactor. Can this be replaced by an enum and color registry.
 * <p>
 * ColorTable is a convenience class which contains a set of factory methods to
 * generate Color objects for each of the colours defined in the Netscape Colour
 * Table.
 * </p>
 * 
 * <table BORDER="1" BGCOLOR="#FFFFFF" CELLPADDING="5" * CELLSPACING="4" width="90%">
 * <TR>
 * <TD ALIGN="center" BGCOLOR="aliceblue" WIDTH="15%">aliceblue<BR>
 * F0F8FF</TD>
 * <TD ALIGN="center" BGCOLOR="antiquewhite" WIDTH="15%">antiquewhite<BR>
 * FAEBD7</TD>
 * <TD ALIGN="center" BGCOLOR="aqua" WIDTH="15%">aqua<BR>
 * 00FFFF</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="aquamarine" WIDTH="15%">aquamarine<BR>
 * 7FFFD4</TD>
 * <TD ALIGN="center" BGCOLOR="azure" WIDTH="15%">azure<BR>
 * F0FFFF</TD>
 * <TD ALIGN="center" BGCOLOR="beige" WIDTH="15%">beige<BR>
 * F5F5DC</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="bisque" WIDTH="15%">bisque<BR>
 * FFE4C4</TD>
 * <TD ALIGN="center" BGCOLOR="burlywood" WIDTH="15%">burlywood<BR>
 * DEB887</TD>
 * <TD ALIGN="center" BGCOLOR="blanchedalmond" WIDTH="15%">blanchedalmond<BR>
 * FFEBCD</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="blue" WIDTH="15%">blue<BR>
 * 0000FF</TD>
 * <TD ALIGN="center" BGCOLOR="bluevoilet" WIDTH="15%">blueviolet<BR>
 * 8A2BE2</TD>
 * <TD ALIGN="center" BGCOLOR="brown" WIDTH="15%">brown<BR>
 * A52A2A</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="black" WIDTH="15%"><FONT COLOR="FFFFFF">black<BR>
 * 000000</FONT></TD>
 * <TD ALIGN="center" BGCOLOR="cadetblue" WIDTH="15%">cadetblue<BR>
 * 5F9EA0</TD>
 * <TD ALIGN="center" BGCOLOR="chartreuse" WIDTH="15%">chartreuse<BR>
 * 7FFF00</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="chocolate" WIDTH="15%">chocolate<BR>
 * D2691E</TD>
 * <TD ALIGN="center" BGCOLOR="coral" WIDTH="15%">coral<BR>
 * FF7F50</TD>
 * <TD ALIGN="center" BGCOLOR="cornflowerblue" WIDTH="15%">cornflowerblue<BR>
 * 6495ED</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="cornsilk" WIDTH="15%">cornsilk<BR>
 * FFF8DC</TD>
 * <TD ALIGN="center" BGCOLOR="crimson" WIDTH="15%">crimson<BR>
 * DC143C</TD>
 * <TD ALIGN="center" BGCOLOR="cyan" WIDTH="15%">cyan<BR>
 * 00FFFF</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="darkblue" WIDTH="15%">darkblue<BR>
 * 00008B</TD>
 * <TD ALIGN="center" BGCOLOR="darkcyan" WIDTH="15%">darkcyan<BR>
 * 008B8B</TD>
 * <TD ALIGN="center" BGCOLOR="darkgoldenrod" WIDTH="15%">darkgoldenrod<BR>
 * B8860B</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="darkgray" WIDTH="15%">darkgray<BR>
 * A9A9A9</TD>
 * <TD ALIGN="center" BGCOLOR="darkgreen" WIDTH="15%">darkgreen<BR>
 * 006400</TD>
 * <TD ALIGN="center" BGCOLOR="darkkhaki" WIDTH="15%">darkkhaki<BR>
 * BDB76B</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="darkmagenta" WIDTH="15%">darkmagenta<BR>
 * 8B008B</TD>
 * <TD ALIGN="center" BGCOLOR="darkolivegreen" WIDTH="15%">darkolivegreen<BR>
 * 556B2F</TD>
 * <TD ALIGN="center" BGCOLOR="darkorange" WIDTH="15%">darkorange<BR>
 * FF8C00</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="darkorchid" WIDTH="15%">darkorchid<BR>
 * 9932CC</TD>
 * <TD ALIGN="center" BGCOLOR="darkred" WIDTH="15%">darkred<BR>
 * 8B0000</TD>
 * <TD ALIGN="center" BGCOLOR="darksalmon" WIDTH="15%">darksalmon<BR>
 * E9967A</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="darkseagreen" WIDTH="15%">darkseagreen<BR>
 * 8FBC8F</TD>
 * <TD ALIGN="center" BGCOLOR="darkslateblue" WIDTH="15%">darkslateblue<BR>
 * 483D8B</TD>
 * <TD ALIGN="center" BGCOLOR="darkslategray" WIDTH="15%">darkslategray<BR>
 * 2F4F4F</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="darkturquoise" WIDTH="15%">darkturquoise<BR>
 * 00CED1</TD>
 * <TD ALIGN="center" BGCOLOR="darkviolet" WIDTH="15%">darkviolet<BR>
 * 9400D3</TD>
 * <TD ALIGN="center" BGCOLOR="deeppink" WIDTH="15%">deeppink<BR>
 * FF1493</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="deepskyblue" WIDTH="15%">deepskyblue<BR>
 * 00BFFF</TD>
 * <TD ALIGN="center" BGCOLOR="dimgray" WIDTH="15%">dimgray<BR>
 * 696969</TD>
 * <TD ALIGN="center" BGCOLOR="dodgerblue" WIDTH="15%">dodgerblue<BR>
 * 1E90FF</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="firebrick" WIDTH="15%">firebrick<BR>
 * B22222</TD>
 * <TD ALIGN="center" BGCOLOR="floralwhite" WIDTH="15%">floralwhite<BR>
 * FFFAF0</TD>
 * <TD ALIGN="center" BGCOLOR="forestgreen" WIDTH="15%">forestgreen<BR>
 * 228B22</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="fuchsia" WIDTH="15%">fuchsia<BR>
 * FF00FF</TD>
 * <TD ALIGN="center" BGCOLOR="gainsboro" WIDTH="15%">gainsboro<BR>
 * DCDCDC</TD>
 * <TD ALIGN="center" BGCOLOR="ghostwhite" WIDTH="15%">ghostwhite<BR>
 * F8F8FF</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="gold" WIDTH="15%">gold<BR>
 * FFD700</TD>
 * <TD ALIGN="center" BGCOLOR="goldenrod" WIDTH="15%">goldenrod<BR>
 * DAA520</TD>
 * <TD ALIGN="center" BGCOLOR="gray" WIDTH="15%">gray<BR>
 * 808080</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="green" WIDTH="15%">green<BR>
 * 008000</TD>
 * <TD ALIGN="center" BGCOLOR="greenyellow" WIDTH="15%">greenyellow<BR>
 * ADFF2F</TD>
 * <TD ALIGN="center" BGCOLOR="honeydew" WIDTH="15%">honeydew<BR>
 * F0FFF0</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="hotpink" WIDTH="15%">hotpink<BR>
 * FF69B4</TD>
 * <TD ALIGN="center" BGCOLOR="indianred" WIDTH="15%">indianred<BR>
 * CD5C5C</TD>
 * <TD ALIGN="center" BGCOLOR="indigo" WIDTH="15%">Indigo<BR>
 * 4B0082</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="ivory" WIDTH="15%">ivory<BR>
 * FFFFF0</TD>
 * <TD ALIGN="center" BGCOLOR="khaki" WIDTH="15%">khaki<BR>
 * F0E68C</TD>
 * <TD ALIGN="center" BGCOLOR="lavender" WIDTH="15%">lavender<BR>
 * E6E6FA</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="lavenderblush" WIDTH="15%">lavenderblush<BR>
 * FFF0F5</TD>
 * <TD ALIGN="center" BGCOLOR="lawngreen" WIDTH="15%">lawngreen<BR>
 * 7CFC00</TD>
 * <TD ALIGN="center" BGCOLOR="lemonchiffon" WIDTH="15%">lemonchiffon<BR>
 * FFFACD</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="lightblue" WIDTH="15%">lightblue<BR>
 * ADD8E6</TD>
 * <TD ALIGN="center" BGCOLOR="lightcoral" WIDTH="15%">lightcoral<BR>
 * F08080</TD>
 * <TD ALIGN="center" BGCOLOR="lightcyan" WIDTH="15%">lightcyan<BR>
 * E0FFFF</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="lightgoldenrodyellow" WIDTH="15%">
 * lightgoldenrodyellow<BR>
 * FAFAD2</TD>
 * <TD ALIGN="center" BGCOLOR="lightgreen" WIDTH="15%">lightgreen<BR>
 * 90EE90</TD>
 * <TD ALIGN="center" BGCOLOR="lightgrey" WIDTH="15%">lightgrey<BR>
 * D3D3D3</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="lightpink" WIDTH="15%">lightpink<BR>
 * FFB6C1</TD>
 * <TD ALIGN="center" BGCOLOR="lightsalmon" WIDTH="15%">lightsalmon<BR>
 * FFA07A</TD>
 * <TD ALIGN="center" BGCOLOR="lightseagreen" WIDTH="15%">lightseagreen<BR>
 * 20B2AA</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="lightskyblue" WIDTH="15%">lightskyblue<BR>
 * 87CEFA</TD>
 * <TD ALIGN="center" BGCOLOR="lightslategray" WIDTH="15%">lightslategray<BR>
 * 778899</TD>
 * <TD ALIGN="center" BGCOLOR="lightsteelblue" WIDTH="15%">lightsteelblue<BR>
 * B0C4DE</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="lightyellow" WIDTH="15%">lightyellow<BR>
 * FFFFE0</TD>
 * <TD ALIGN="center" BGCOLOR="lime" WIDTH="15%">lime<BR>
 * 00FF00</TD>
 * <TD ALIGN="center" BGCOLOR="limegreen" WIDTH="15%">limegreen<BR>
 * 32CD32</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="linen" WIDTH="15%"><font COLOR="#000000">linen<BR>
 * FAF0E6</TD>
 * <TD ALIGN="center" BGCOLOR="magenta" WIDTH="15%">magenta<BR>
 * FF00FF</TD>
 * <TD ALIGN="center" BGCOLOR="maroon" WIDTH="15%">maroon<BR>
 * 800000</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="mediumaquamarine" WIDTH="15%">mediumauqamarine<BR>
 * 66CDAA</TD>
 * <TD ALIGN="center" BGCOLOR="mediumblue" WIDTH="15%">mediumblue<BR>
 * 0000CD</TD>
 * <TD ALIGN="center" BGCOLOR="mediumorchid" WIDTH="15%">mediumorchid<BR>
 * BA55D3</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="mediumpurple" WIDTH="15%">mediumpurple<BR>
 * 9370D8</TD>
 * <TD ALIGN="center" BGCOLOR="mediumseagreen" WIDTH="15%">mediumseagreen<BR>
 * 3CB371</TD>
 * <TD ALIGN="center" BGCOLOR="mediumslateblue" WIDTH="15%">mediumslateblue<BR>
 * 7B68EE</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="mediumspringgreen" WIDTH="15%">mediumspringgreen<BR>
 * 00FA9A</TD>
 * <TD ALIGN="center" BGCOLOR="mediumturquoise" WIDTH="15%">mediumturquoise<BR>
 * 48D1CC</TD>
 * <TD ALIGN="center" BGCOLOR="mediumvioletred" WIDTH="15%">mediumvioletred<BR>
 * C71585</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="midnightblue" WIDTH="15%">midnightblue<BR>
 * 191970</TD>
 * <TD ALIGN="center" BGCOLOR="mintcream" WIDTH="15%">mintcream<BR>
 * F5FFFA</TD>
 * <TD ALIGN="center" BGCOLOR="mistyrose" WIDTH="15%">mistyrose<BR>
 * FFE4E1</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="moccasin" WIDTH="15%">moccasin<BR>
 * FFE4B5</TD>
 * <TD ALIGN="center" BGCOLOR="navajowhite" WIDTH="15%">navajowhite<BR>
 * FFDEAD</TD>
 * <TD ALIGN="center" BGCOLOR="navy" WIDTH="15%">navy<BR>
 * 000080</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="oldlace" WIDTH="15%">oldlace<BR>
 * FDF5E6</TD>
 * <TD ALIGN="center" BGCOLOR="olive" WIDTH="15%">olive<BR>
 * 808000</TD>
 * <TD ALIGN="center" BGCOLOR="olivedrab" WIDTH="15%">olivedrab<BR>
 * 688E23</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="orange" WIDTH="15%">orange<BR>
 * FFA500</TD>
 * <TD ALIGN="center" BGCOLOR="orangered" WIDTH="15%">orangered<BR>
 * FF4500</TD>
 * <TD ALIGN="center" BGCOLOR="orchid" WIDTH="15%">orchid<BR>
 * DA70D6</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="palegoldenrod" WIDTH="15%">palegoldenrod<BR>
 * EEE8AA</TD>
 * <TD ALIGN="center" BGCOLOR="palegreen" WIDTH="15%">palegreen<BR>
 * 98FB98</TD>
 * <TD ALIGN="center" BGCOLOR="paleturquoise" WIDTH="15%">paleturquoise<BR>
 * AFEEEE</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="palevioletred" WIDTH="15%">palevioletred<BR>
 * D87093</TD>
 * <TD ALIGN="center" BGCOLOR="papayawhip" WIDTH="15%">papayawhip<BR>
 * FFEFD5</TD>
 * <TD ALIGN="center" BGCOLOR="peachpuff" WIDTH="15%">peachpuff<BR>
 * FFDAB9</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="peru" WIDTH="15%">peru<BR>
 * CD853F</TD>
 * <TD ALIGN="center" BGCOLOR="pink" WIDTH="15%">pink<BR>
 * FFC0CB</TD>
 * <TD ALIGN="center" BGCOLOR="plum" WIDTH="15%">plum<BR>
 * DDA0DD</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="powderblue" WIDTH="15%">powderblue<BR>
 * B0E0E6</TD>
 * <TD ALIGN="center" BGCOLOR="purple" WIDTH="15%">purple<BR>
 * 800080</TD>
 * <TD ALIGN="center" BGCOLOR="red" WIDTH="15%">red<BR>
 * FF0000</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="rosybrown" WIDTH="15%">rosybrown<BR>
 * BC8F8F</TD>
 * <TD ALIGN="center" BGCOLOR="royalblue" WIDTH="15%">royalblue<BR>
 * 4169E1</TD>
 * <TD ALIGN="center" BGCOLOR="saddlebrown" WIDTH="15%">saddlebrown<BR>
 * 8B4513</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="salmon" WIDTH="15%">salmon<BR>
 * FA8072</TD>
 * <TD ALIGN="center" BGCOLOR="sandybrown" WIDTH="15%">sandybrown<BR>
 * F4A460</TD>
 * <TD ALIGN="center" BGCOLOR="seagreen" WIDTH="15%">seagreen<BR>
 * 2E8B57</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="seashell" WIDTH="15%">seashell<BR>
 * FFF5EE</TD>
 * <TD ALIGN="center" BGCOLOR="sienna" WIDTH="15%">sienna<BR>
 * A0522D</TD>
 * <TD ALIGN="center" BGCOLOR="silver" WIDTH="15%">silver<BR>
 * C0C0C0</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="skyblue" WIDTH="15%">skyblue<BR>
 * 87CEEB</TD>
 * <TD ALIGN="center" BGCOLOR="slateblue" WIDTH="15%">slateblue<BR>
 * 6A5ACD</TD>
 * <TD ALIGN="center" BGCOLOR="slategray" WIDTH="15%">slategray<BR>
 * 708090</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="snow" WIDTH="15%">snow<BR>
 * FFFAFA</TD>
 * <TD ALIGN="center" BGCOLOR="springgreen" WIDTH="15%">springgreen<BR>
 * 00FF7F</TD>
 * <TD ALIGN="center" BGCOLOR="steelblue" WIDTH="15%">steelblue<BR>
 * 4682B4</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="tan" WIDTH="15%">tan<BR>
 * D2B48C</TD>
 * <TD ALIGN="center" BGCOLOR="teal" WIDTH="15%">teal<BR>
 * 008080</TD>
 * <TD ALIGN="center" BGCOLOR="thistle" WIDTH="15%">thistle<BR>
 * D8BFD8</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="tomato" WIDTH="15%">tomato<BR>
 * FF6347</TD>
 * <TD ALIGN="center" BGCOLOR="turquoise" WIDTH="15%">turquoise<BR>
 * 40E0D0</TD>
 * <TD ALIGN="center" BGCOLOR="violet" WIDTH="15%">violet<BR>
 * EE82EE</TD>
 * </TR>
 * <TR>
 * <TD ALIGN="center" BGCOLOR="wheat" WIDTH="15%">wheat<BR>
 * F5DEB3<BR>
 * </TD>
 * <TD ALIGN="center" BGCOLOR="white" WIDTH="15%">white<BR>
 * FFFFFF</TD>
 * <TD ALIGN="center" BGCOLOR="whitesmoke" WIDTH="15%">whitesmoke<BR>
 * F5F5F5</TD>
 * </TR>
 * </table>
 */
public enum WebPalette {

	ALICE_BLUE(0x00F0F8FF),
	ANTIQUE_WHITE(0x00FAEBD7),
	AQUA(0x0000FFFF),
	AQUAMARINE(0x007FFFD4),
	AZURE(0x00F0FFFF),
	BEIGE(0x00F5F5DC),
	BISQUE(0x00FFE4C4),
	BLACK(0x00000000),
	BLANCHED_ALMOND(0x00FFEBCD),
	BLUE(0x000000FF),
	BLUE_VIOLET(0x008A2BE2),
	BROWN(0x00A52A2A),
	BURLYWOOD(0x00DEB887),
	CADET_BLUE(0x005F9EA0),
	CHARTREUSE(0x007FFF00),
	CHOCOLATE(0x00D2691E),
	CORAL(0x00FF7F50),
	CORNFLOWER_BLUE(0x006495ED),
	CORNSILK(0x00FFF8DC),
	CRIMSON(0x00DC143C),
	CYAN(0x0000FFFF),
	DARK_BLUE(0x0000008B),
	DARK_CYAN(0x00008B8B),
	DARK_GOLDENROD(0x00B8860B),
	DARK_GRAY(0x00A9A9A9),
	DARK_GREEN(0x00006400),
	DARK_KHAKI(0x00BDB76B),
	DARK_MAGENTA(0x008B008B),
	DARK_OLIVE_GREEN(0x00556B2F),
	DARK_ORANGE(0x00FF8C00),
	DARK_ORCHID(0x009932CC),
	DARK_RED(0x008B0000),
	DARK_SALMON(0x00E9967A),
	DARK_SEA_GREEN(0x008FBC8F),
	DARK_SLATE_BLUE(0x00483D8B),
	DARK_SLATE_GRAY(0x002F4F4F),
	DARK_TURQUOISE(0x0000CED1),
	DARK_VIOLET(0x009400D3),
	DEEP_PINK(0x00FF1493),
	DEEP_SKY_BLUE(0x0000BFFF),
	DIM_GRAY(0x00696969),
	DODGER_BLUE(0x001E90FF),
	FIREBRICK(0x00B22222),
	FLORAL_WHITE(0x00FFFAF0),
	FOREST_GREEN(0x00228B22),
	FUCHSIA(0x00FF00FF),
	GAINSBORO(0x00DCDCDC),
	GHOST_WHITE(0x00F8F8FB),
	GOLD(0x00FFD700),
	GOLDENROD(0x00DAA520),
	GRAY(0x00808080),
	GREEN(0x00008000),
	GREEN_YELLOW(0x00ADFF2F),
	HONEYDEW(0x00F0FFF0),
	HOT_PINK(0x00FF69B4),
	INDIAN_RED(0x00CD5C5C),
	INDIGO(0x004B0082),
	IVORY(0x00FFFFF0),
	KHAKI(0x00F0E68C),
	LAVENDER(0x00E6E6FA),
	LAVENDER_BLUSH(0x00FFF0F5),
	LAWN_GREEN(0x007CFC00),
	LEMON_CHIFFON(0x00FFFACD),
	LIGHT_BLUE(0x00ADD8E6),
	LIGHT_CORAL(0x00F08080),
	LIGHT_CYAN(0x00E0FFFF),
	LIGHT_GOLDENROD_YELLOW(0x00FAFAD2),
	LIGHT_GREEN(0x0090EE90),
	LIGHT_GREY(0x00D3D3D3),
	LIGHT_PINK(0x00FFB6C1),
	LIGHT_SALMON(0x00FFA07A),
	LIGHT_SEA_GREEN(0x0020B2AA),
	LIGHT_SKY_BLUE(0x0087CEFA),
	LIGHT_SLATE_GRAY(0x00778899),
	LIGHT_STEEL_BLUE(0x00B0C4DE),
	LIGHT_YELLOW(0x00FFFFE0),
	LIME(0x0000FF00),
	LIME_GREEN(0x0032CD32),
	LINEN(0x00FAF0E6),
	MAGENTA(0x00FF00FF),
	MAROON(0x00800000),
	MEDIUM_AQUAMARINE(0x0066CDAA),
	MEDIUM_BLUE(0x000000CD),
	MEDIUM_ORCHID(0x00BA55D3),
	MEDIUM_PURPLE(0x009370DB),
	MEDIUM_SEAGREEN(0x003CB371),
	MEDIUM_SLATEBLUE(0x007B68EE),
	MEDIUM_SPRINGGREEN(0x0000FA9A),
	MEDIUM_TURQUOISE(0x0048D1CC),
	MEDIUM_VIOLETRED(0x00C71585),
	MIDNIGHT_BLUE(0x00191970),
	MINT_CREAM(0x00F5FFFA),
	MISTY_ROSE(0x00FFE4E1),
	MOCCASIN(0x00FFE4B5),
	NAVAJO_WHITE(0x00FFDEAD),
	NAVY(0x00000080),
	OLD_LACE(0x00FDF5E6),
	OLIVE(0x00808000),
	OLIVE_DRAB(0x006B8E23),
	ORANGE(0x00FFA500),
	ORANGE_RED(0x00FF4500),
	ORCHID(0x00DA70D6),
	PALE_GOLDENROD(0x00EEE8AA),
	PALE_GREEN(0x0098FB98),
	PALE_TURQUOISE(0x00AFEEEE),
	PALE_VIOLET_RED(0x00DB7093),
	PAPAYA_WHIP(0x00FFEFD5),
	PEACH_PUFF(0x00FFDAB9),
	PERU(0x00CD853F),
	PINK(0x00FFC0CB),
	PLUM(0x00DDA0DD),
	POWDER_BLUE(0x00B0E0E6),
	PURPLE(0x00800080),
	RED(0x00FF0000),
	ROSY_BROWN(0x00BC8F8F),
	ROYAL_BLUE(0x004169E1),
	SADDLE_BROWN(0x008B4513),
	SALMON(0x00FA8072),
	SANDY_BROWN(0x00F4A460),
	SEA_GREEN(0x002E8B57),
	SEASHELL(0x00FFF5EE),
	SIENNA(0x00A0522D),
	SILVER(0x00C0C0C0),
	SKY_BLUE(0x0087CEEB),
	SLATE_BLUE(0x006A5ACD),
	SLATE_GRAY(0x00708090),
	SNOW(0x00FFFAFA),
	SPRING_GREEN(0x0000FF7F),
	STEEL_BLUE(0x004682B4),
	TAN(0x00D2B48C),
	TEAL(0x00008080),
	THISTLE(0x00D8BFD8),
	TOMATO(0x00FF6347),
	TURQUOISE(0x0040E0D0),
	VIOLET(0x00EE82EE),
	WHEAT(0x00F5DEB3),
	WHITE(0x00FFFFFF),
	WHITE_SMOKE(0x00F5F5F5),
	YELLOW(0x00FFFF00),
	YELLOW_GREEN(0x009ACD32);

	private Color color;
	
	private WebPalette(int rgb) {
		color = new Color(rgb);
	}

	public Color color() {
		return color;
	}
}