/*
  Sample.java
  Copyright © 2010 David M. Anderson

  A list of numbers (doubles), together with statistics.
*/

package us.EpsilonDelta.SimpleStats;

import us.EpsilonDelta.math.Stats;
import java.util.*;


//*****************************************************************************


class Sample
{                                                                      //Sample
//-----------------------------------------------------------------------------

    public
    Sample( List< Double > values )
    {
        set( values );
    }

//.............................................................................

    public
    Sample( String valuesText )
    {
        NumberParser.DoublesResult parseResult
                = NumberParser.parseToDoubles( valuesText );
        set( parseResult.getNumbers() );
        m_precision = parseResult.getPrecision();
    }

//-----------------------------------------------------------------------------

    private
    void
    set( List< Double > values )
    {
        m_values = values;
        m_size = values.size();
        Collections.sort( values );
        if ( m_size > 0 )
        {
            m_min = m_values.get( 0 );
            m_max = m_values.get( m_size - 1 );
            m_median = Stats.median( m_values, true );
            m_mean = Stats.mean( m_values );
            if ( m_size > 1 )
            {
                m_variance = Stats.variance( m_values, m_mean );
            }
        }
    }

//=============================================================================

    public
    int
    size( )
    {
        return m_size;
    }

//-----------------------------------------------------------------------------
    
    public
    double
    min( )
    {
        return m_min;
    }

//-----------------------------------------------------------------------------
    
    public
    double
    max( )
    {
        return m_max;
    }

//-----------------------------------------------------------------------------
    
    public
    double
    median( )
    {
        return m_median;
    }

//-----------------------------------------------------------------------------
    
    public
    double
    mean( )
    {
        return m_mean;
    }

//-----------------------------------------------------------------------------
    
    public
    double
    variance( )
    {
        return m_variance;
    }

//-----------------------------------------------------------------------------
    
    public
    int
    precision( )
    {
        return m_precision;
    }

//=============================================================================

    private List< Double > m_values;
    private int m_size;
    private double m_min;
    private double m_max;
    private double m_median;
    private double m_mean;
    private double m_variance;
    private int m_precision = 3;
    
//-----------------------------------------------------------------------------
}                                                                      //Sample


//*****************************************************************************
