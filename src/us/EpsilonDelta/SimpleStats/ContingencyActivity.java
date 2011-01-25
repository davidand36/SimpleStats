/*
  ContingencyActivity.java
  Copyright © 2010 David M. Anderson

  Activity for input and analysis of a contingency table.
*/

package us.EpsilonDelta.SimpleStats;

import us.EpsilonDelta.math.Stats;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import android.widget.EditText;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
// import android.util.Log;


//*****************************************************************************


public
class ContingencyActivity
    extends StatsActivity
{                                                         //ContingencyActivity
//-----------------------------------------------------------------------------

    @Override
    protected
    int
    getLayoutId( )
    {
        return R.layout.contingency;
    }

//-----------------------------------------------------------------------------
    
    @Override
    protected
    int
    getHelpTextId( )
    {
        return R.string.contingency_help_text;
    }

//=============================================================================

    @Override
    protected
    CharSequence
    getAnalysisText( )
    {
        String numRowsText
                = ((EditText)findViewById( R.id.contingency_numRows ))
                .getText().toString();
        String numColsText
                = ((EditText)findViewById( R.id.contingency_numCols ))
                .getText().toString();
        int numRows = 0;
        int numCols = 0;
        try
        {
            numRows = Integer.parseInt( numRowsText );
            numCols = Integer.parseInt( numColsText );
        }
        catch ( NumberFormatException e ) { } //ignore
        if ( (numRows < 2) || (numCols < 2) )
        {
            return getResources().getString(
                R.string.contingency_tooFewRowsColsError );
        }
        
        String sampleText
                = ((EditText)findViewById( R.id.contingency_sampleData ))
                .getText().toString();
        List< Integer > numbers = NumberParser.parseToIntegers( sampleText );
        if ( numbers.size() != numRows * numCols )
        {
            return getResources().getString(
                R.string.contingency_wrongNumberOfEntriesError )
                    + " " + numbers.size();
        }

        Integer[][] table = new Integer[ numRows ][];
        int numbersIdx = 0;
        for ( int i = 0; i < numRows; ++i )
        {
            table[ i ] = new Integer[ numCols ];
            for ( int j = 0; j < numCols; ++j )
            {
                table[ i ][ j ] = numbers.get( numbersIdx++ );
            }
        }

        CharSequence analysisText = writeAnalysisText( table );
        return analysisText;
    }

//-----------------------------------------------------------------------------

    private
    CharSequence
    writeAnalysisText( Integer[][] table )
    {
        StringWriter reportSW = new StringWriter( );
        PrintWriter reportPW = new PrintWriter( reportSW );
        List< StringUtil.TextSpan > textSpans
                = new ArrayList< StringUtil.TextSpan >( );

        String NegativeCountError_ = getResources().getString(
            R.string.contingency_negativeCountError );
        String TooFewRowsColsError_ = getResources().getString(
            R.string.contingency_tooFewRowsColsError );
        String TestOfIndependence_
                = getResources().getString( R.string.TestOfIndependence );
        String FishersExactTest_
                = getResources().getString( R.string.FishersExactTest );
        String oneTailed_ = getResources().getString( R.string.oneTailed );
        String twoTailed_ = getResources().getString( R.string.twoTailed );
        String dof_ = getResources().getString( R.string.dof );
        String Probability_ = getResources().getString( R.string.Probability );
        String YatesCorrection_
                = getResources().getString( R.string.YatesCorrection );

        //Extra line ends and spaces if in portrait orientation:
        String lend1 = (isPortrait()  ?  "\n "  :  "");
        
        boolean is2x2 = ((table.length == 2) && (table[ 0 ].length == 2));

        boolean negativeCell = false;
        for ( Integer[] row : table )
            for ( Integer cell : row )
                if ( cell < 0 )
                    negativeCell = true;
        
        if ( negativeCell )
        {
            reportPW.printf( NegativeCountError_ );
        }
        else if ( is2x2 )
        {
            Stats.ContingencyTableResult contingencyRslt
                    = Stats.chiSquareContingencyTableTest( table, false );

            printTotals( reportPW, contingencyRslt );
            
            if ( contingencyRslt.chiSquareResult.degreesOfFreedom < 1 )
            {
                reportPW.printf( TooFewRowsColsError_ );
            }
            else
            {
                reportPW.printf( TestOfIndependence_ + ":\n"
                                 + "  χ²=%.3f (" + dof_ + "=%d)" + lend1
                                 + " " + Probability_ 
                                 + " (" + oneTailed_ + ") = ",
                             contingencyRslt.chiSquareResult.chiSquare,
                             contingencyRslt.chiSquareResult.degreesOfFreedom );
                int start = reportSW.toString().length();

                reportPW.printf(  "%.3f",
                             contingencyRslt.chiSquareResult.probability / 2. );

                int end = reportSW.toString().length();
                boolean preferFisher = (contingencyRslt.minExpectedCellFreq
                                        < MIN_CHISQUARE_FREQ);
                if ( ! preferFisher )
                    textSpans.add(
                        new StringUtil.TextSpan( start, end,
                                             new StyleSpan( Typeface.BOLD ) ) );

                if ( contingencyRslt.sampleTotal < MAX_FISHER_SAMPLE_TOTAL )
                {
                    double fisherProb = Stats.fishersExactTest( table, 1 );

                    reportPW.printf( "\n\n"
                                     + FishersExactTest_ + ":\n"
                                     + "  " + Probability_
                                     + " (" + oneTailed_ + ") = " );
                    start = reportSW.toString().length();
                    reportPW.printf( "%.3f", fisherProb );
                    end = reportSW.toString().length();
                    if ( preferFisher )
                        textSpans.add(
                            new StringUtil.TextSpan( start, end,
                                             new StyleSpan( Typeface.BOLD ) ) );

                    fisherProb = Stats.fishersExactTest( table, 2 );
                    reportPW.printf( "\n"
                                     + "  " + Probability_
                                     + " (" + twoTailed_ + ") = " );
                    start = reportSW.toString().length();
                    reportPW.printf( "%.3f", fisherProb );
                }
                else
                {
                    contingencyRslt
                           = Stats.chiSquareContingencyTableTest( table, true );

                    reportPW.printf( "\n\n"
                                     + "  χ² (" + YatesCorrection_ + "): "
                                     + "%.3f (" + dof_ + ": %d)" + lend1
                                     + " " + Probability_ 
                                     + " (" + oneTailed_ + ") = ",
                             contingencyRslt.chiSquareResult.chiSquare,
                             contingencyRslt.chiSquareResult.degreesOfFreedom );
                    start = reportSW.toString().length();

                    reportPW.printf(  "%.3f",
                             contingencyRslt.chiSquareResult.probability / 2. );

                    end = reportSW.toString().length();
                    if ( preferFisher )
                        textSpans.add(
                            new StringUtil.TextSpan( start, end,
                                             new StyleSpan( Typeface.BOLD ) ) );
                }
            }
        }
        else
        {
            Stats.ContingencyTableResult contingencyRslt
                    = Stats.chiSquareContingencyTableTest( table, false );

            printTotals( reportPW, contingencyRslt );
            
            if ( contingencyRslt.chiSquareResult.degreesOfFreedom < 1 )
            {
                reportPW.printf( TooFewRowsColsError_ );
            }
            else
            {
                reportPW.printf( TestOfIndependence_ + ":\n"
                                 + "  χ²=%.3f (" + dof_ + "=%d)" + lend1
                                 + " " + Probability_ 
                                 + " (" + twoTailed_ + ") = ",
                             contingencyRslt.chiSquareResult.chiSquare,
                             contingencyRslt.chiSquareResult.degreesOfFreedom );
                int start = reportSW.toString().length();

                reportPW.printf(  "%.3f",
                                  contingencyRslt.chiSquareResult.probability );

                int end = reportSW.toString().length();
                textSpans.add(
                    new StringUtil.TextSpan( start, end,
                                             new StyleSpan( Typeface.BOLD ) ) );
            }
        }
        
        return StringUtil.makeSpannableString( reportSW.toString(), textSpans );
    }

//-----------------------------------------------------------------------------

    private
    void
    printTotals( PrintWriter reportPW,
                 Stats.ContingencyTableResult contingencyRslt )
    {
        String SampleSize_ = getResources().getString(
            R.string.contingency_sampleSize );
        String RowTotals_ = getResources().getString(
            R.string.contingency_rowTotals );
        String ColumnTotals_ = getResources().getString(
            R.string.contingency_columnTotals );

        reportPW.printf( SampleSize_ + "=%d\n",
                         contingencyRslt.sampleTotal );
        reportPW.printf( RowTotals_ + ": " );
        for ( int i = 0; i < contingencyRslt.rowTotals.length; ++i )
        {
            if ( i > 0 )
                reportPW.printf( ", " );
            reportPW.printf( "%d", contingencyRslt.rowTotals[ i ] );
        }
        reportPW.printf( "\n" );
        reportPW.printf( ColumnTotals_ + ": " );
        for ( int i = 0; i < contingencyRslt.columnTotals.length; ++i )
        {
            if ( i > 0 )
                reportPW.printf( ", " );
            reportPW.printf( "%d", contingencyRslt.columnTotals[ i ] );
        }
        reportPW.printf( "\n\n" );
    }
    
//=============================================================================

    private static final int MIN_CHISQUARE_FREQ = 5;
    private static final int MAX_FISHER_SAMPLE_TOTAL = 1000;
    // private static final String LOGTAG = "ContingencyActivity";

//-----------------------------------------------------------------------------
}                                                         //ContingencyActivity


//*****************************************************************************
