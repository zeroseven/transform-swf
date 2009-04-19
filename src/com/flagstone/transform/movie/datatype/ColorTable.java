/*
 * ColorTable.java
 * Transform
 * 
 * Copyright (c) 2001-2008 Flagstone Software Ltd. All rights reserved.
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
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.movie.datatype;

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
@SuppressWarnings("PMD.TooManyMethods")
public final class ColorTable {
	/** Returns a new Color object with the definition for aliceblue. */
	public static Color aliceblue() {
		return new Color(0xF0, 0xF8, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for antiquewhite. */
	public static Color antiquewhite() {
		return new Color(0xFA, 0xEB, 0xD7, 0xFF);
	}

	/** Returns a new Color object with the definition for aqua. */
	public static Color aqua() {
		return new Color(0x00, 0xFF, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for aquamarine. */
	public static Color aquamarine() {
		return new Color(0x7F, 0xFF, 0xD4, 0xFF);
	}

	/** Returns a new Color object with the definition for azure. */
	public static Color azure() {
		return new Color(0xF0, 0xFF, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for beige. */
	public static Color beige() {
		return new Color(0xF5, 0xF5, 0xDC, 0xFF);
	}

	/** Returns a new Color object with the definition for bisque. */
	public static Color bisque() {
		return new Color(0xFF, 0xE4, 0xC4, 0xFF);
	}

	/** Returns a new Color object with the definition for black. */
	public static Color black() {
		return new Color(0x00, 0x00, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for blanchedalmond. */
	public static Color blanchedalmond() {
		return new Color(0xFF, 0xEB, 0xCD, 0xFF);
	}

	/** Returns a new Color object with the definition for blue. */
	public static Color blue() {
		return new Color(0x00, 0x00, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for blueviolet. */
	public static Color blueviolet() {
		return new Color(0x8A, 0x2B, 0xE2, 0xFF);
	}

	/** Returns a new Color object with the definition for brown. */
	public static Color brown() {
		return new Color(0xA5, 0x2A, 0x2A, 0xFF);
	}

	/** Returns a new Color object with the definition for burlywood. */
	public static Color burlywood() {
		return new Color(0xDE, 0xB8, 0x87, 0xFF);
	}

	/** Returns a new Color object with the definition for cadetblue. */
	public static Color cadetblue() {
		return new Color(0x5F, 0x9E, 0xA0, 0xFF);
	}

	/** Returns a new Color object with the definition for chartreuse. */
	public static Color chartreuse() {
		return new Color(0x7F, 0xFF, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for chocolate. */
	public static Color chocolate() {
		return new Color(0xD2, 0x69, 0x1E, 0xFF);
	}

	/** Returns a new Color object with the definition for coral. */
	public static Color coral() {
		return new Color(0xFF, 0x7F, 0x50, 0xFF);
	}

	/** Returns a new Color object with the definition for cornflowerblue. */
	public static Color cornflowerblue() {
		return new Color(0x64, 0x95, 0xED, 0xFF);
	}

	/** Returns a new Color object with the definition for cornsilk. */
	public static Color cornsilk() {
		return new Color(0xFF, 0xF8, 0xDC, 0xFF);
	}

	/** Returns a new Color object with the definition for crimson. */
	public static Color crimson() {
		return new Color(0xDC, 0x14, 0x3C, 0xFF);
	}

	/** Returns a new Color object with the definition for cyan. */
	public static Color cyan() {
		return new Color(0x00, 0xFF, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for darkblue. */
	public static Color darkblue() {
		return new Color(0x00, 0x00, 0x8B, 0xFF);
	}

	/** Returns a new Color object with the definition for darkcyan. */
	public static Color darkcyan() {
		return new Color(0x00, 0x8B, 0x8B, 0xFF);
	}

	/** Returns a new Color object with the definition for darkgoldenrod. */
	public static Color darkgoldenrod() {
		return new Color(0xB8, 0x86, 0x0B, 0xFF);
	}

	/** Returns a new Color object with the definition for darkgray. */
	public static Color darkgray() {
		return new Color(0xA9, 0xA9, 0xA9, 0xFF);
	}

	/** Returns a new Color object with the definition for darkgreen. */
	public static Color darkgreen() {
		return new Color(0x00, 0x64, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for darkkhaki. */
	public static Color darkkhaki() {
		return new Color(0xBD, 0xB7, 0x6B, 0xFF);
	}

	/** Returns a new Color object with the definition for darkmagenta. */
	public static Color darkmagenta() {
		return new Color(0x8B, 0x00, 0x8B, 0xFF);
	}

	/** Returns a new Color object with the definition for darkolivegreen. */
	public static Color darkolivegreen() {
		return new Color(0x55, 0x6B, 0x2F, 0xFF);
	}

	/** Returns a new Color object with the definition for darkorange. */
	public static Color darkorange() {
		return new Color(0xFF, 0x8C, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for darkorchid. */
	public static Color darkorchid() {
		return new Color(0x99, 0x32, 0xCC, 0xFF);
	}

	/** Returns a new Color object with the definition for darkred. */
	public static Color darkred() {
		return new Color(0x8B, 0x00, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for darksalmon. */
	public static Color darksalmon() {
		return new Color(0xE9, 0x96, 0x7A, 0xFF);
	}

	/** Returns a new Color object with the definition for darkseagreen. */
	public static Color darkseagreen() {
		return new Color(0x8F, 0xBC, 0x8F, 0xFF);
	}

	/** Returns a new Color object with the definition for darkslateblue. */
	public static Color darkslateblue() {
		return new Color(0x48, 0x3D, 0x8B, 0xFF);
	}

	/** Returns a new Color object with the definition for darkslategray. */
	public static Color darkslategray() {
		return new Color(0x2F, 0x4F, 0x4F, 0xFF);
	}

	/** Returns a new Color object with the definition for darkturquoise. */
	public static Color darkturquoise() {
		return new Color(0x00, 0xCE, 0xD1, 0xFF);
	}

	/** Returns a new Color object with the definition for darkviolet. */
	public static Color darkviolet() {
		return new Color(0x94, 0x00, 0xD3, 0xFF);
	}

	/** Returns a new Color object with the definition for deeppink. */
	public static Color deeppink() {
		return new Color(0xFF, 0x14, 0x93, 0xFF);
	}

	/** Returns a new Color object with the definition for deepskyblue. */
	public static Color deepskyblue() {
		return new Color(0x00, 0xBF, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for dimgray. */
	public static Color dimgray() {
		return new Color(0x69, 0x69, 0x69, 0xFF);
	}

	/** Returns a new Color object with the definition for dodgerblue. */
	public static Color dodgerblue() {
		return new Color(0x1E, 0x90, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for firebrick. */
	public static Color firebrick() {
		return new Color(0xB2, 0x22, 0x22, 0xFF);
	}

	/** Returns a new Color object with the definition for floralwhite. */
	public static Color floralwhite() {
		return new Color(0xFF, 0xFA, 0xF0, 0xFF);
	}

	/** Returns a new Color object with the definition for forestgreen. */
	public static Color forestgreen() {
		return new Color(0x22, 0x8B, 0x22, 0xFF);
	}

	/** Returns a new Color object with the definition for fuchsia. */
	public static Color fuchsia() {
		return new Color(0xFF, 0x00, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for gainsboro. */
	public static Color gainsboro() {
		return new Color(0xDC, 0xDC, 0xDC, 0xFF);
	}

	/** Returns a new Color object with the definition for ghostwhite. */
	public static Color ghostwhite() {
		return new Color(0xF8, 0xF8, 0xFB, 0xFF);
	}

	/** Returns a new Color object with the definition for gold. */
	public static Color gold() {
		return new Color(0xFF, 0xD7, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for goldenrod. */
	public static Color goldenrod() {
		return new Color(0xDA, 0xA5, 0x20, 0xFF);
	}

	/** Returns a new Color object with the definition for gray. */
	public static Color gray() {
		return new Color(0x80, 0x80, 0x80, 0xFF);
	}

	/** Returns a new Color object with the definition for green. */
	public static Color green() {
		return new Color(0x00, 0x80, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for greenyellow. */
	public static Color greenyellow() {
		return new Color(0xAD, 0xFF, 0x2F, 0xFF);
	}

	/** Returns a new Color object with the definition for honeydew. */
	public static Color honeydew() {
		return new Color(0xF0, 0xFF, 0xF0, 0xFF);
	}

	/** Returns a new Color object with the definition for hotpink. */
	public static Color hotpink() {
		return new Color(0xFF, 0x69, 0xB4, 0xFF);
	}

	/** Returns a new Color object with the definition for indianred. */
	public static Color indianred() {
		return new Color(0xCD, 0x5C, 0x5C, 0xFF);
	}

	/** Returns a new Color object with the definition for indigo. */
	public static Color indigo() {
		return new Color(0x4B, 0x00, 0x82, 0xFF);
	}

	/** Returns a new Color object with the definition for ivory. */
	public static Color ivory() {
		return new Color(0xFF, 0xFF, 0xF0, 0xFF);
	}

	/** Returns a new Color object with the definition for khaki. */
	public static Color khaki() {
		return new Color(0xF0, 0xE6, 0x8C, 0xFF);
	}

	/** Returns a new Color object with the definition for lavender. */
	public static Color lavender() {
		return new Color(0xE6, 0xE6, 0xFA, 0xFF);
	}

	/** Returns a new Color object with the definition for lavenderblush. */
	public static Color lavenderblush() {
		return new Color(0xFF, 0xF0, 0xF5, 0xFF);
	}

	/** Returns a new Color object with the definition for lawngreen. */
	public static Color lawngreen() {
		return new Color(0x7C, 0xFC, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for lemonchiffon. */
	public static Color lemonchiffon() {
		return new Color(0xFF, 0xFA, 0xCD, 0xFF);
	}

	/** Returns a new Color object with the definition for lightblue. */
	public static Color lightblue() {
		return new Color(0xAD, 0xD8, 0xE6, 0xFF);
	}

	/** Returns a new Color object with the definition for lightcoral. */
	public static Color lightcoral() {
		return new Color(0xF0, 0x80, 0x80, 0xFF);
	}

	/** Returns a new Color object with the definition for lightcyan. */
	public static Color lightcyan() {
		return new Color(0xE0, 0xFF, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for lightgoldenrodyellow. */
	public static Color lightgoldenrodyellow() {
		return new Color(0xFA, 0xFA, 0xD2, 0xFF);
	}

	/** Returns a new Color object with the definition for lightgreen. */
	public static Color lightgreen() {
		return new Color(0x90, 0xEE, 0x90, 0xFF);
	}

	/** Returns a new Color object with the definition for lightgrey. */
	public static Color lightgrey() {
		return new Color(0xD3, 0xD3, 0xD3, 0xFF);
	}

	/** Returns a new Color object with the definition for lightpink. */
	public static Color lightpink() {
		return new Color(0xFF, 0xB6, 0xC1, 0xFF);
	}

	/** Returns a new Color object with the definition for lightsalmon. */
	public static Color lightsalmon() {
		return new Color(0xFF, 0xA0, 0x7A, 0xFF);
	}

	/** Returns a new Color object with the definition for lightseagreen. */
	public static Color lightseagreen() {
		return new Color(0x20, 0xB2, 0xAA, 0xFF);
	}

	/** Returns a new Color object with the definition for lightskyblue. */
	public static Color lightskyblue() {
		return new Color(0x87, 0xCE, 0xFA, 0xFF);
	}

	/** Returns a new Color object with the definition for lightslategray. */
	public static Color lightslategray() {
		return new Color(0x77, 0x88, 0x99, 0xFF);
	}

	/** Returns a new Color object with the definition for lightsteelblue. */
	public static Color lightsteelblue() {
		return new Color(0xB0, 0xC4, 0xDE, 0xFF);
	}

	/** Returns a new Color object with the definition for lightyellow. */
	public static Color lightyellow() {
		return new Color(0xFF, 0xFF, 0xE0, 0xFF);
	}

	/** Returns a new Color object with the definition for lime. */
	public static Color lime() {
		return new Color(0x00, 0xFF, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for limegreen. */
	public static Color limegreen() {
		return new Color(0x32, 0xCD, 0x32, 0xFF);
	}

	/** Returns a new Color object with the definition for linen. */
	public static Color linen() {
		return new Color(0xFA, 0xF0, 0xE6, 0xFF);
	}

	/** Returns a new Color object with the definition for magenta. */
	public static Color magenta() {
		return new Color(0xFF, 0x00, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for maroon. */
	public static Color maroon() {
		return new Color(0x80, 0x00, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for mediumaquamarine. */
	public static Color mediumaquamarine() {
		return new Color(0x66, 0xCD, 0xAA, 0xFF);
	}

	/** Returns a new Color object with the definition for mediumblue. */
	public static Color mediumblue() {
		return new Color(0x00, 0x00, 0xCD, 0xFF);
	}

	/** Returns a new Color object with the definition for mediumorchid. */
	public static Color mediumorchid() {
		return new Color(0xBA, 0x55, 0xD3, 0xFF);
	}

	/** Returns a new Color object with the definition for mediumpurple. */
	public static Color mediumpurple() {
		return new Color(0x93, 0x70, 0xDB, 0xFF);
	}

	/** Returns a new Color object with the definition for mediumseagreen. */
	public static Color mediumseagreen() {
		return new Color(0x3C, 0xB3, 0x71, 0xFF);
	}

	/** Returns a new Color object with the definition for mediumslateblue. */
	public static Color mediumslateblue() {
		return new Color(0x7B, 0x68, 0xEE, 0xFF);
	}

	/** Returns a new Color object with the definition for mediumspringgreen. */
	public static Color mediumspringgreen() {
		return new Color(0x00, 0xFA, 0x9A, 0xFF);
	}

	/** Returns a new Color object with the definition for mediumturquoise. */
	public static Color mediumturquoise() {
		return new Color(0x48, 0xD1, 0xCC, 0xFF);
	}

	/** Returns a new Color object with the definition for mediumvioletred. */
	public static Color mediumvioletred() {
		return new Color(0xC7, 0x15, 0x85, 0xFF);
	}

	/** Returns a new Color object with the definition for midnightblue. */
	public static Color midnightblue() {
		return new Color(0x19, 0x19, 0x70, 0xFF);
	}

	/** Returns a new Color object with the definition for mintcream. */
	public static Color mintcream() {
		return new Color(0xF5, 0xFF, 0xFA, 0xFF);
	}

	/** Returns a new Color object with the definition for mistyrose. */
	public static Color mistyrose() {
		return new Color(0xFF, 0xE4, 0xE1, 0xFF);
	}

	/** Returns a new Color object with the definition for moccasin. */
	public static Color moccasin() {
		return new Color(0xFF, 0xE4, 0xB5, 0xFF);
	}

	/** Returns a new Color object with the definition for navajowhite. */
	public static Color navajowhite() {
		return new Color(0xFF, 0xDE, 0xAD, 0xFF);
	}

	/** Returns a new Color object with the definition for navy. */
	public static Color navy() {
		return new Color(0x00, 0x00, 0x80, 0xFF);
	}

	/** Returns a new Color object with the definition for oldlace. */
	public static Color oldlace() {
		return new Color(0xFD, 0xF5, 0xE6, 0xFF);
	}

	/** Returns a new Color object with the definition for olive. */
	public static Color olive() {
		return new Color(0x80, 0x80, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for olivedrab. */
	public static Color olivedrab() {
		return new Color(0x6B, 0x8E, 0x23, 0xFF);
	}

	/** Returns a new Color object with the definition for orange. */
	public static Color orange() {
		return new Color(0xFF, 0xA5, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for orangered. */
	public static Color orangered() {
		return new Color(0xFF, 0x45, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for orchid. */
	public static Color orchid() {
		return new Color(0xDA, 0x70, 0xD6, 0xFF);
	}

	/** Returns a new Color object with the definition for palegoldenrod. */
	public static Color palegoldenrod() {
		return new Color(0xEE, 0xE8, 0xAA, 0xFF);
	}

	/** Returns a new Color object with the definition for palegreen. */
	public static Color palegreen() {
		return new Color(0x98, 0xFB, 0x98, 0xFF);
	}

	/** Returns a new Color object with the definition for paleturquoise. */
	public static Color paleturquoise() {
		return new Color(0xAF, 0xEE, 0xEE, 0xFF);
	}

	/** Returns a new Color object with the definition for palevioletred. */
	public static Color palevioletred() {
		return new Color(0xDB, 0x70, 0x93, 0xFF);
	}

	/** Returns a new Color object with the definition for papayawhip. */
	public static Color papayawhip() {
		return new Color(0xFF, 0xEF, 0xD5, 0xFF);
	}

	/** Returns a new Color object with the definition for peachpuff. */
	public static Color peachpuff() {
		return new Color(0xFF, 0xDA, 0xB9, 0xFF);
	}

	/** Returns a new Color object with the definition for peru. */
	public static Color peru() {
		return new Color(0xCD, 0x85, 0x3F, 0xFF);
	}

	/** Returns a new Color object with the definition for pink. */
	public static Color pink() {
		return new Color(0xFF, 0xC0, 0xCB, 0xFF);
	}

	/** Returns a new Color object with the definition for plum. */
	public static Color plum() {
		return new Color(0xDD, 0xA0, 0xDD, 0xFF);
	}

	/** Returns a new Color object with the definition for powderblue. */
	public static Color powderblue() {
		return new Color(0xB0, 0xE0, 0xE6, 0xFF);
	}

	/** Returns a new Color object with the definition for purple. */
	public static Color purple() {
		return new Color(0x80, 0x00, 0x80, 0xFF);
	}

	/** Returns a new Color object with the definition for red. */
	public static Color red() {
		return new Color(0xFF, 0x00, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for rosybrown. */
	public static Color rosybrown() {
		return new Color(0xBC, 0x8F, 0x8F, 0xFF);
	}

	/** Returns a new Color object with the definition for royalblue. */
	public static Color royalblue() {
		return new Color(0x41, 0x69, 0xE1, 0xFF);
	}

	/** Returns a new Color object with the definition for saddlebrown. */
	public static Color saddlebrown() {
		return new Color(0x8B, 0x45, 0x13, 0xFF);
	}

	/** Returns a new Color object with the definition for salmon. */
	public static Color salmon() {
		return new Color(0xFA, 0x80, 0x72, 0xFF);
	}

	/** Returns a new Color object with the definition for sandybrown. */
	public static Color sandybrown() {
		return new Color(0xF4, 0xA4, 0x60, 0xFF);
	}

	/** Returns a new Color object with the definition for seagreen. */
	public static Color seagreen() {
		return new Color(0x2E, 0x8B, 0x57, 0xFF);
	}

	/** Returns a new Color object with the definition for seashell. */
	public static Color seashell() {
		return new Color(0xFF, 0xF5, 0xEE, 0xFF);
	}

	/** Returns a new Color object with the definition for sienna. */
	public static Color sienna() {
		return new Color(0xA0, 0x52, 0x2d, 0xFF);
	}

	/** Returns a new Color object with the definition for silver. */
	public static Color silver() {
		return new Color(0xC0, 0xC0, 0xC0, 0xFF);
	}

	/** Returns a new Color object with the definition for skyblue. */
	public static Color skyblue() {
		return new Color(0x87, 0xCE, 0xEB, 0xFF);
	}

	/** Returns a new Color object with the definition for slateblue. */
	public static Color slateblue() {
		return new Color(0x6A, 0x5A, 0xCD, 0xFF);
	}

	/** Returns a new Color object with the definition for slategray. */
	public static Color slategray() {
		return new Color(0x70, 0x80, 0x90, 0xFF);
	}

	/** Returns a new Color object with the definition for snow. */
	public static Color snow() {
		return new Color(0xFF, 0xFA, 0xFA, 0xFF);
	}

	/** Returns a new Color object with the definition for springgreen. */
	public static Color springgreen() {
		return new Color(0x00, 0xFF, 0x7F, 0xFF);
	}

	/** Returns a new Color object with the definition for steelblue. */
	public static Color steelblue() {
		return new Color(0x46, 0x82, 0xB4, 0xFF);
	}

	/** Returns a new Color object with the definition for tan. */
	public static Color tan() {
		return new Color(0xD2, 0xB4, 0x8C, 0xFF);
	}

	/** Returns a new Color object with the definition for teal. */
	public static Color teal() {
		return new Color(0x00, 0x80, 0x80, 0xFF);
	}

	/** Returns a new Color object with the definition for thistle. */
	public static Color thistle() {
		return new Color(0xD8, 0xBF, 0xD8, 0xFF);
	}

	/** Returns a new Color object with the definition for tomato. */
	public static Color tomato() {
		return new Color(0xFF, 0x63, 0x47, 0xFF);
	}

	/** Returns a new Color object with the definition for turquoise. */
	public static Color turquoise() {
		return new Color(0x40, 0xE0, 0xD0, 0xFF);
	}

	/** Returns a new Color object with the definition for violet. */
	public static Color violet() {
		return new Color(0xEE, 0x82, 0xEE, 0xFF);
	}

	/** Returns a new Color object with the definition for wheat. */
	public static Color wheat() {
		return new Color(0xF5, 0xDE, 0xB3, 0xFF);
	}

	/** Returns a new Color object with the definition for white. */
	public static Color white() {
		return new Color(0xFF, 0xFF, 0xFF, 0xFF);
	}

	/** Returns a new Color object with the definition for whitesmoke. */
	public static Color whitesmoke() {
		return new Color(0xF5, 0xF5, 0xF5, 0xFF);
	}

	/** Returns a new Color object with the definition for yellow. */
	public static Color yellow() {
		return new Color(0xFF, 0xFF, 0x00, 0xFF);
	}

	/** Returns a new Color object with the definition for yellowgreen. */
	public static Color yellowgreen() {
		return new Color(0x9A, 0xCD, 0x32, 0xFF);
	}

	/**
	 * Returns a new transparent Color object with the definition for aliceblue.
	 */
	public static Color aliceblue(final int alpha) {
		return new Color(0xF0, 0xF8, 0xFF, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * antiquewhite.
	 */
	public static Color antiquewhite(final int alpha) {
		return new Color(0xFA, 0xEB, 0xD7, alpha);
	}

	/** Returns a new transparent Color object with the definition for aqua. */
	public static Color aqua(final int alpha) {
		return new Color(0x00, 0xFF, 0xFF, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * aquamarine.
	 */
	public static Color aquamarine(final int alpha) {
		return new Color(0x7F, 0xFF, 0xD4, alpha);
	}

	/** Returns a new transparent Color object with the definition for azure. */
	public static Color azure(final int alpha) {
		return new Color(0xF0, 0xFF, 0xFF, alpha);
	}

	/** Returns a new transparent Color object with the definition for beige. */
	public static Color beige(final int alpha) {
		return new Color(0xF5, 0xF5, 0xDC, alpha);
	}

	/** Returns a new transparent Color object with the definition for bisque. */
	public static Color bisque(final int alpha) {
		return new Color(0xFF, 0xE4, 0xC4, alpha);
	}

	/** Returns a new transparent Color object with the definition for black. */
	public static Color black(final int alpha) {
		return new Color(0x00, 0x00, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * blanchedalmond.
	 */
	public static Color blanchedalmond(final int alpha) {
		return new Color(0xFF, 0xEB, 0xCD, alpha);
	}

	/** Returns a new transparent Color object with the definition for blue. */
	public static Color blue(final int alpha) {
		return new Color(0x00, 0x00, 0xFF, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * blueviolet.
	 */
	public static Color blueviolet(final int alpha) {
		return new Color(0x8A, 0x2B, 0xE2, alpha);
	}

	/** Returns a new transparent Color object with the definition for brown. */
	public static Color brown(final int alpha) {
		return new Color(0xA5, 0x2A, 0x2A, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for burlywood.
	 */
	public static Color burlywood(final int alpha) {
		return new Color(0xDE, 0xB8, 0x87, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for cadetblue.
	 */
	public static Color cadetblue(final int alpha) {
		return new Color(0x5F, 0x9E, 0xA0, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * chartreuse.
	 */
	public static Color chartreuse(final int alpha) {
		return new Color(0x7F, 0xFF, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for chocolate.
	 */
	public static Color chocolate(final int alpha) {
		return new Color(0xD2, 0x69, 0x1E, alpha);
	}

	/** Returns a new transparent Color object with the definition for coral. */
	public static Color coral(final int alpha) {
		return new Color(0xFF, 0x7F, 0x50, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * cornflowerblue.
	 */
	public static Color cornflowerblue(final int alpha) {
		return new Color(0x64, 0x95, 0xED, alpha);
	}

	/** Returns a new transparent Color object with the definition for cornsilk. */
	public static Color cornsilk(final int alpha) {
		return new Color(0xFF, 0xF8, 0xDC, alpha);
	}

	/** Returns a new transparent Color object with the definition for crimson. */
	public static Color crimson(final int alpha) {
		return new Color(0xDC, 0x14, 0x3C, alpha);
	}

	/** Returns a new transparent Color object with the definition for cyan. */
	public static Color cyan(final int alpha) {
		return new Color(0x00, 0xFF, 0xFF, alpha);
	}

	/** Returns a new transparent Color object with the definition for darkblue. */
	public static Color darkblue(final int alpha) {
		return new Color(0x00, 0x00, 0x8B, alpha);
	}

	/** Returns a new transparent Color object with the definition for darkcyan. */
	public static Color darkcyan(final int alpha) {
		return new Color(0x00, 0x8B, 0x8B, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darkgoldenrod.
	 */
	public static Color darkgoldenrod(final int alpha) {
		return new Color(0xB8, 0x86, 0x0B, alpha);
	}

	/** Returns a new transparent Color object with the definition for darkgray. */
	public static Color darkgray(final int alpha) {
		return new Color(0xA9, 0xA9, 0xA9, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for darkgreen.
	 */
	public static Color darkgreen(final int alpha) {
		return new Color(0x00, 0x64, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for darkkhaki.
	 */
	public static Color darkkhaki(final int alpha) {
		return new Color(0xBD, 0xB7, 0x6B, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darkmagenta.
	 */
	public static Color darkmagenta(final int alpha) {
		return new Color(0x8B, 0x00, 0x8B, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darkolivegreen.
	 */
	public static Color darkolivegreen(final int alpha) {
		return new Color(0x55, 0x6B, 0x2F, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darkorange.
	 */
	public static Color darkorange(final int alpha) {
		return new Color(0xFF, 0x8C, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darkorchid.
	 */
	public static Color darkorchid(final int alpha) {
		return new Color(0x99, 0x32, 0xCC, alpha);
	}

	/** Returns a new transparent Color object with the definition for darkred. */
	public static Color darkred(final int alpha) {
		return new Color(0x8B, 0x00, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darksalmon.
	 */
	public static Color darksalmon(final int alpha) {
		return new Color(0xE9, 0x96, 0x7A, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darkseagreen.
	 */
	public static Color darkseagreen(final int alpha) {
		return new Color(0x8F, 0xBC, 0x8F, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darkslateblue.
	 */
	public static Color darkslateblue(final int alpha) {
		return new Color(0x48, 0x3D, 0x8B, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darkslategray.
	 */
	public static Color darkslategray(final int alpha) {
		return new Color(0x2F, 0x4F, 0x4F, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darkturquoise.
	 */
	public static Color darkturquoise(final int alpha) {
		return new Color(0x00, 0xCE, 0xD1, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * darkviolet.
	 */
	public static Color darkviolet(final int alpha) {
		return new Color(0x94, 0x00, 0xD3, alpha);
	}

	/** Returns a new transparent Color object with the definition for deeppink. */
	public static Color deeppink(final int alpha) {
		return new Color(0xFF, 0x14, 0x93, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * deepskyblue.
	 */
	public static Color deepskyblue(final int alpha) {
		return new Color(0x00, 0xBF, 0xFF, alpha);
	}

	/** Returns a new transparent Color object with the definition for dimgray. */
	public static Color dimgray(final int alpha) {
		return new Color(0x69, 0x69, 0x69, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * dodgerblue.
	 */
	public static Color dodgerblue(final int alpha) {
		return new Color(0x1E, 0x90, 0xFF, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for firebrick.
	 */
	public static Color firebrick(final int alpha) {
		return new Color(0xB2, 0x22, 0x22, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * floralwhite.
	 */
	public static Color floralwhite(final int alpha) {
		return new Color(0xFF, 0xFA, 0xF0, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * forestgreen.
	 */
	public static Color forestgreen(final int alpha) {
		return new Color(0x22, 0x8B, 0x22, alpha);
	}

	/** Returns a new transparent Color object with the definition for fuchsia. */
	public static Color fuchsia(final int alpha) {
		return new Color(0xFF, 0x00, 0xFF, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for gainsboro.
	 */
	public static Color gainsboro(final int alpha) {
		return new Color(0xDC, 0xDC, 0xDC, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * ghostwhite.
	 */
	public static Color ghostwhite(final int alpha) {
		return new Color(0xF8, 0xF8, 0xFB, alpha);
	}

	/** Returns a new transparent Color object with the definition for gold. */
	public static Color gold(final int alpha) {
		return new Color(0xFF, 0xD7, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for goldenrod.
	 */
	public static Color goldenrod(final int alpha) {
		return new Color(0xDA, 0xA5, 0x20, alpha);
	}

	/** Returns a new transparent Color object with the definition for gray. */
	public static Color gray(final int alpha) {
		return new Color(0x80, 0x80, 0x80, alpha);
	}

	/** Returns a new transparent Color object with the definition for green. */
	public static Color green(final int alpha) {
		return new Color(0x00, 0x80, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * greenyellow.
	 */
	public static Color greenyellow(final int alpha) {
		return new Color(0xAD, 0xFF, 0x2F, alpha);
	}

	/** Returns a new transparent Color object with the definition for honeydew. */
	public static Color honeydew(final int alpha) {
		return new Color(0xF0, 0xFF, 0xF0, alpha);
	}

	/** Returns a new transparent Color object with the definition for hotpink. */
	public static Color hotpink(final int alpha) {
		return new Color(0xFF, 0x69, 0xB4, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for indianred.
	 */
	public static Color indianred(final int alpha) {
		return new Color(0xCD, 0x5C, 0x5C, alpha);
	}

	/** Returns a new transparent Color object with the definition for indigo. */
	public static Color indigo(final int alpha) {
		return new Color(0x4B, 0x00, 0x82, alpha);
	}

	/** Returns a new transparent Color object with the definition for ivory. */
	public static Color ivory(final int alpha) {
		return new Color(0xFF, 0xFF, 0xF0, alpha);
	}

	/** Returns a new transparent Color object with the definition for khaki. */
	public static Color khaki(final int alpha) {
		return new Color(0xF0, 0xE6, 0x8C, alpha);
	}

	/** Returns a new transparent Color object with the definition for lavender. */
	public static Color lavender(final int alpha) {
		return new Color(0xE6, 0xE6, 0xFA, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lavenderblush.
	 */
	public static Color lavenderblush(final int alpha) {
		return new Color(0xFF, 0xF0, 0xF5, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for lawngreen.
	 */
	public static Color lawngreen(final int alpha) {
		return new Color(0x7C, 0xFC, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lemonchiffon.
	 */
	public static Color lemonchiffon(final int alpha) {
		return new Color(0xFF, 0xFA, 0xCD, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for lightblue.
	 */
	public static Color lightblue(final int alpha) {
		return new Color(0xAD, 0xD8, 0xE6, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lightcoral.
	 */
	public static Color lightcoral(final int alpha) {
		return new Color(0xF0, 0x80, 0x80, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for lightcyan.
	 */
	public static Color lightcyan(final int alpha) {
		return new Color(0xE0, 0xFF, 0xFF, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lightgoldenrodyellow.
	 */
	public static Color lightgoldenrodyellow(final int alpha) {
		return new Color(0xFA, 0xFA, 0xD2, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lightgreen.
	 */
	public static Color lightgreen(final int alpha) {
		return new Color(0x90, 0xEE, 0x90, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for lightgrey.
	 */
	public static Color lightgrey(final int alpha) {
		return new Color(0xD3, 0xD3, 0xD3, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for lightpink.
	 */
	public static Color lightpink(final int alpha) {
		return new Color(0xFF, 0xB6, 0xC1, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lightsalmon.
	 */
	public static Color lightsalmon(final int alpha) {
		return new Color(0xFF, 0xA0, 0x7A, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lightseagreen.
	 */
	public static Color lightseagreen(final int alpha) {
		return new Color(0x20, 0xB2, 0xAA, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lightskyblue.
	 */
	public static Color lightskyblue(final int alpha) {
		return new Color(0x87, 0xCE, 0xFA, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lightslategray.
	 */
	public static Color lightslategray(final int alpha) {
		return new Color(0x77, 0x88, 0x99, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lightsteelblue.
	 */
	public static Color lightsteelblue(final int alpha) {
		return new Color(0xB0, 0xC4, 0xDE, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * lightyellow.
	 */
	public static Color lightyellow(final int alpha) {
		return new Color(0xFF, 0xFF, 0xE0, alpha);
	}

	/** Returns a new transparent Color object with the definition for lime. */
	public static Color lime(final int alpha) {
		return new Color(0x00, 0xFF, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for limegreen.
	 */
	public static Color limegreen(final int alpha) {
		return new Color(0x32, 0xCD, 0x32, alpha);
	}

	/** Returns a new transparent Color object with the definition for linen. */
	public static Color linen(final int alpha) {
		return new Color(0xFA, 0xF0, 0xE6, alpha);
	}

	/** Returns a new transparent Color object with the definition for magenta. */
	public static Color magenta(final int alpha) {
		return new Color(0xFF, 0x00, 0xFF, alpha);
	}

	/** Returns a new transparent Color object with the definition for maroon. */
	public static Color maroon(final int alpha) {
		return new Color(0x80, 0x00, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * mediumaquamarine.
	 */
	public static Color mediumaquamarine(final int alpha) {
		return new Color(0x66, 0xCD, 0xAA, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * mediumblue.
	 */
	public static Color mediumblue(final int alpha) {
		return new Color(0x00, 0x00, 0xCD, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * mediumorchid.
	 */
	public static Color mediumorchid(final int alpha) {
		return new Color(0xBA, 0x55, 0xD3, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * mediumpurple.
	 */
	public static Color mediumpurple(final int alpha) {
		return new Color(0x93, 0x70, 0xDB, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * mediumseagreen.
	 */
	public static Color mediumseagreen(final int alpha) {
		return new Color(0x3C, 0xB3, 0x71, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * mediumslateblue.
	 */
	public static Color mediumslateblue(final int alpha) {
		return new Color(0x7B, 0x68, 0xEE, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * mediumspringgreen.
	 */
	public static Color mediumspringgreen(final int alpha) {
		return new Color(0x00, 0xFA, 0x9A, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * mediumturquoise.
	 */
	public static Color mediumturquoise(final int alpha) {
		return new Color(0x48, 0xD1, 0xCC, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * mediumvioletred.
	 */
	public static Color mediumvioletred(final int alpha) {
		return new Color(0xC7, 0x15, 0x85, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * midnightblue.
	 */
	public static Color midnightblue(final int alpha) {
		return new Color(0x19, 0x19, 0x70, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for mintcream.
	 */
	public static Color mintcream(final int alpha) {
		return new Color(0xF5, 0xFF, 0xFA, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for mistyrose.
	 */
	public static Color mistyrose(final int alpha) {
		return new Color(0xFF, 0xE4, 0xE1, alpha);
	}

	/** Returns a new transparent Color object with the definition for moccasin. */
	public static Color moccasin(final int alpha) {
		return new Color(0xFF, 0xE4, 0xB5, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * navajowhite.
	 */
	public static Color navajowhite(final int alpha) {
		return new Color(0xFF, 0xDE, 0xAD, alpha);
	}

	/** Returns a new transparent Color object with the definition for navy. */
	public static Color navy(final int alpha) {
		return new Color(0x00, 0x00, 0x80, alpha);
	}

	/** Returns a new transparent Color object with the definition for oldlace. */
	public static Color oldlace(final int alpha) {
		return new Color(0xFD, 0xF5, 0xE6, alpha);
	}

	/** Returns a new transparent Color object with the definition for olive. */
	public static Color olive(final int alpha) {
		return new Color(0x80, 0x80, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for olivedrab.
	 */
	public static Color olivedrab(final int alpha) {
		return new Color(0x6B, 0x8E, 0x23, alpha);
	}

	/** Returns a new transparent Color object with the definition for orange. */
	public static Color orange(final int alpha) {
		return new Color(0xFF, 0xA5, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for orangered.
	 */
	public static Color orangered(final int alpha) {
		return new Color(0xFF, 0x45, 0x00, alpha);
	}

	/** Returns a new transparent Color object with the definition for orchid. */
	public static Color orchid(final int alpha) {
		return new Color(0xDA, 0x70, 0xD6, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * palegoldenrod.
	 */
	public static Color palegoldenrod(final int alpha) {
		return new Color(0xEE, 0xE8, 0xAA, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for palegreen.
	 */
	public static Color palegreen(final int alpha) {
		return new Color(0x98, 0xFB, 0x98, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * paleturquoise.
	 */
	public static Color paleturquoise(final int alpha) {
		return new Color(0xAF, 0xEE, 0xEE, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * palevioletred.
	 */
	public static Color palevioletred(final int alpha) {
		return new Color(0xDB, 0x70, 0x93, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * papayawhip.
	 */
	public static Color papayawhip(final int alpha) {
		return new Color(0xFF, 0xEF, 0xD5, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for peachpuff.
	 */
	public static Color peachpuff(final int alpha) {
		return new Color(0xFF, 0xDA, 0xB9, alpha);
	}

	/** Returns a new transparent Color object with the definition for peru. */
	public static Color peru(final int alpha) {
		return new Color(0xCD, 0x85, 0x3F, alpha);
	}

	/** Returns a new transparent Color object with the definition for pink. */
	public static Color pink(final int alpha) {
		return new Color(0xFF, 0xC0, 0xCB, alpha);
	}

	/** Returns a new transparent Color object with the definition for plum. */
	public static Color plum(final int alpha) {
		return new Color(0xDD, 0xA0, 0xDD, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * powderblue.
	 */
	public static Color powderblue(final int alpha) {
		return new Color(0xB0, 0xE0, 0xE6, alpha);
	}

	/** Returns a new transparent Color object with the definition for purple. */
	public static Color purple(final int alpha) {
		return new Color(0x80, 0x00, 0x80, alpha);
	}

	/** Returns a new transparent Color object with the definition for red. */
	public static Color red(final int alpha) {
		return new Color(0xFF, 0x00, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for rosybrown.
	 */
	public static Color rosybrown(final int alpha) {
		return new Color(0xBC, 0x8F, 0x8F, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for royalblue.
	 */
	public static Color royalblue(final int alpha) {
		return new Color(0x41, 0x69, 0xE1, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * saddlebrown.
	 */
	public static Color saddlebrown(final int alpha) {
		return new Color(0x8B, 0x45, 0x13, alpha);
	}

	/** Returns a new transparent Color object with the definition for salmon. */
	public static Color salmon(final int alpha) {
		return new Color(0xFA, 0x80, 0x72, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * sandybrown.
	 */
	public static Color sandybrown(final int alpha) {
		return new Color(0xF4, 0xA4, 0x60, alpha);
	}

	/** Returns a new transparent Color object with the definition for seagreen. */
	public static Color seagreen(final int alpha) {
		return new Color(0x2E, 0x8B, 0x57, alpha);
	}

	/** Returns a new transparent Color object with the definition for seashell. */
	public static Color seashell(final int alpha) {
		return new Color(0xFF, 0xF5, 0xEE, alpha);
	}

	/** Returns a new transparent Color object with the definition for sienna. */
	public static Color sienna(final int alpha) {
		return new Color(0xA0, 0x52, 0x2d, alpha);
	}

	/** Returns a new transparent Color object with the definition for silver. */
	public static Color silver(final int alpha) {
		return new Color(0xC0, 0xC0, 0xC0, alpha);
	}

	/** Returns a new transparent Color object with the definition for skyblue. */
	public static Color skyblue(final int alpha) {
		return new Color(0x87, 0xCE, 0xEB, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for slateblue.
	 */
	public static Color slateblue(final int alpha) {
		return new Color(0x6A, 0x5A, 0xCD, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for slategray.
	 */
	public static Color slategray(final int alpha) {
		return new Color(0x70, 0x80, 0x90, alpha);
	}

	/** Returns a new transparent Color object with the definition for snow. */
	public static Color snow(final int alpha) {
		return new Color(0xFF, 0xFA, 0xFA, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * springgreen.
	 */
	public static Color springgreen(final int alpha) {
		return new Color(0x00, 0xFF, 0x7F, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for steelblue.
	 */
	public static Color steelblue(final int alpha) {
		return new Color(0x46, 0x82, 0xB4, alpha);
	}

	/** Returns a new transparent Color object with the definition for tan. */
	public static Color tan(final int alpha) {
		return new Color(0xD2, 0xB4, 0x8C, alpha);
	}

	/** Returns a new transparent Color object with the definition for teal. */
	public static Color teal(final int alpha) {
		return new Color(0x00, 0x80, 0x80, alpha);
	}

	/** Returns a new transparent Color object with the definition for thistle. */
	public static Color thistle(final int alpha) {
		return new Color(0xD8, 0xBF, 0xD8, alpha);
	}

	/** Returns a new transparent Color object with the definition for tomato. */
	public static Color tomato(final int alpha) {
		return new Color(0xFF, 0x63, 0x47, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for turquoise.
	 */
	public static Color turquoise(final int alpha) {
		return new Color(0x40, 0xE0, 0xD0, alpha);
	}

	/** Returns a new transparent Color object with the definition for violet. */
	public static Color violet(final int alpha) {
		return new Color(0xEE, 0x82, 0xEE, alpha);
	}

	/** Returns a new transparent Color object with the definition for wheat. */
	public static Color wheat(final int alpha) {
		return new Color(0xF5, 0xDE, 0xB3, alpha);
	}

	/** Returns a new transparent Color object with the definition for white. */
	public static Color white(final int alpha) {
		return new Color(0xFF, 0xFF, 0xFF, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * whitesmoke.
	 */
	public static Color whitesmoke(final int alpha) {
		return new Color(0xF5, 0xF5, 0xF5, alpha);
	}

	/** Returns a new transparent Color object with the definition for yellow. */
	public static Color yellow(final int alpha) {
		return new Color(0xFF, 0xFF, 0x00, alpha);
	}

	/**
	 * Returns a new transparent Color object with the definition for
	 * yellowgreen.
	 */
	public static Color yellowgreen(final int alpha) {
		return new Color(0x9A, 0xCD, 0x32, alpha);
	}

	private ColorTable() {
	}
}