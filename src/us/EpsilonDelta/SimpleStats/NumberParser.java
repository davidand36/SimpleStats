/*
  NumberParser.java
  Copyright © 2010 David M. Anderson

  Routines for extracting numbers from a string and determining the precision
  expressed by a numeric string.
*/

package us.EpsilonDelta.SimpleStats;

import java.util.*;
import java.util.regex.*;


//*****************************************************************************


public
class NumberParser
{                                                                //NumberParser
//-----------------------------------------------------------------------------

    public static
    DoublesResult
    parseToDoubles( String text )
    {
        ArrayList< Double > numbers = new ArrayList< Double >();
        Matcher matcher = ms_doublePattern.matcher( text );
        int precision = 0;
        while ( matcher.find() )
        {
            String dblStr = matcher.group();
            Double d = new Double( dblStr );
            numbers.add( d );
            precision = Math.max( precision, getPrecision( dblStr ) );
        }
        return new DoublesResult( numbers, precision );
    }

//-----------------------------------------------------------------------------

    public static
    int
    getPrecision( String numberString )
    {
        int pointPos = numberString.indexOf( '.' );
        if ( pointPos >= 0 )
            return numberString.length() - pointPos - 1;
        return 0;
    }

//=============================================================================

    public static
    List< Integer >
    parseToIntegers( String text )
    {
        ArrayList< Integer > numbers = new ArrayList< Integer >();
        Matcher matcher = ms_integerPattern.matcher( text );
        while ( matcher.find() )
        {
            String intStr = matcher.group();
            Integer i = new Integer( intStr );
            numbers.add( i );
        }
        return numbers;
    }
    
//=============================================================================

    public static
    class DoublesResult
    {                                                           //DoublesResult
    //-------------------------------------------------------------------------

        public
        DoublesResult( List< Double > numbers, int precision )
        {
            m_numbers = numbers;
            m_precision = precision;
        }

    //=========================================================================

        public
        List< Double >
        getNumbers( )
        {
            return m_numbers;
        }

    //-------------------------------------------------------------------------

        public
        int
        getPrecision( )
        {
            return m_precision;
        }
        
    //=========================================================================

        private List< Double > m_numbers;
        private int m_precision;
        
    //-------------------------------------------------------------------------
    }                                                           //DoublesResult

//=============================================================================

    private static final Pattern ms_doublePattern =
            Pattern.compile( "-?(\\d+(\\.\\d*)?|\\.\\d+)" );
    private static final Pattern ms_integerPattern =
            Pattern.compile( "-?\\d+" );
    
//-----------------------------------------------------------------------------
}                                                                //NumberParser


//*****************************************************************************
