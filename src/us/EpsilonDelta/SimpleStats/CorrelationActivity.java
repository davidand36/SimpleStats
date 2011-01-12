/*
  CorrelationActivity.java
  Copyright © 2010 David M. Anderson

  Activity for input and analysis of a paired numeric sample.
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
//import android.util.Log;


//*****************************************************************************


public
class CorrelationActivity
    extends StatsActivity
{                                                         //CorrelationActivity
//-----------------------------------------------------------------------------

    @Override
    protected
    int
    getLayoutId( )
    {
        return R.layout.correlation;
    }

//=============================================================================

    @Override
    protected
    CharSequence
    getAnalysisText( )
    {
        String sampleText
                = ((EditText)findViewById( R.id.correlation_sampleData ))
                .getText().toString();
        NumberParser.DoublesResult parseResult
                = NumberParser.parseToDoubles( sampleText );
        List< Double > numbers = parseResult.getNumbers();
        if ( numbers.size() % 2 != 0 )
        {
            return getResources().getString(
                R.string.correlation_oddNumberError )
                    + numbers.size();
        }
        int sampleSize = numbers.size() / 2;
        List< double[] > pairs = new ArrayList< double[] >( sampleSize );
        for ( int i = 0; i < sampleSize; ++i )
        {
            double[] pair = new double[ 2 ];
            pair[ 0 ] = numbers.get( i*2 );
            pair[ 1 ] = numbers.get( i*2 + 1 );
            pairs.add( pair );
        }
        int precision = parseResult.getPrecision();
        CharSequence analysisText = writeAnalysisText( pairs, precision );
        return analysisText;
    }

//-----------------------------------------------------------------------------

    private
    CharSequence
    writeAnalysisText( List< double[] > pairs, int precision )
    {
        StringWriter reportSW = new StringWriter( );
        PrintWriter reportPW = new PrintWriter( reportSW );
        List< TextSpan > textSpans = new ArrayList< TextSpan >( );
        String statFmt2 = "%." + (precision + 2) + "f";

        String Sample_ = getResources().getString( R.string.Sample );
        String size_ = getResources().getString( R.string.size );
        String Min_ = getResources().getString( R.string.Min );
        String Max_ = getResources().getString( R.string.Max );
        String Mean_ = getResources().getString( R.string.Mean );
        String StdDev_ = getResources().getString( R.string.StdDev );
        String PearsonR_ = getResources().getString( R.string.PearsonR );
        String TestOfH0_ = getResources().getString( R.string.TestOfH0 );
        String dof_ = getResources().getString( R.string.dof );
        String Probability_ = getResources().getString( R.string.Probability );
        String LinearRegression_
                = getResources().getString( R.string.LinearRegression );

        //Extra line ends and spaces if in portrait orientation:
        String lend1 = (isPortrait()  ?  "\n "  :  "");
        String lend2 = (isPortrait()  ?  "\n "  :  "");
        
        reportPW.printf( Sample_ + " " + size_ + "=%d", pairs.size() );
        if ( pairs.size() > 2 )
        {
            Stats.CorrelationTestResult corrRslt
                    = Stats.linearCorrelationTest( pairs, m_tail );
            reportPW.printf( "\n"
                             + "X: " + Mean_ + "=" + statFmt2 + lend2
                             + " " + StdDev_ + "=" + statFmt2 + "\n"
                             + "Y: " + Mean_ + "=" + statFmt2 + lend2
                             + " " + StdDev_ + "=" + statFmt2 + "\n"
                             + PearsonR_ + "=",
                             corrRslt.mean0,
                             Math.sqrt( corrRslt.variance0 ),
                             corrRslt.mean1,
                             Math.sqrt( corrRslt.variance1 ) );
            int start = reportSW.toString().length();

            reportPW.printf( "%.3f", corrRslt.r );
            
            int end = reportSW.toString().length();
            textSpans.add( new TextSpan( start, end,
                                         new StyleSpan( Typeface.BOLD ) ) );
            
            reportPW.printf( "\n\n"
                             + TestOfH0_
                             + " (ρ" + m_tail.h0RelationSymbol + "0):\n"
                             + "  t=%.3f (" + dof_ + "=%d)" + lend1
                             + " " + Probability_ + " = ",
                             corrRslt.tResult.t,
                             corrRslt.tResult.degreesOfFreedom );
            start = reportSW.toString().length();

            reportPW.printf(  "%.3f", corrRslt.tResult.probability );

            end = reportSW.toString().length();
            textSpans.add( new TextSpan( start, end,
                                         new StyleSpan( Typeface.BOLD ) ) );

            if ( corrRslt.variance0 > 0. )
            {
                Stats.SimpleRegressionResult regressionRslt
                        = Stats.simpleLinearRegression(
                            pairs.size(),
                            corrRslt.mean0,
                            corrRslt.mean1,
                            corrRslt.variance0,
                            corrRslt.variance1,
                            corrRslt.r );
                
                String betaFmt = "%." + 3 + "f";
                reportPW.printf( "\n\n"
                                 + LinearRegression_ + lend1
                                 + " (Y = α + ßX + ε):\n"
                                 + "α=" + statFmt2 + lend1
                                 + " (" + StdDev_ + "=" + statFmt2 + ")\n"
                                 + "ß=" + betaFmt + lend1
                                 + " (" + StdDev_ + "=" + betaFmt + ")\n"
                                 + "ε: " + StdDev_ + "=" + statFmt2,
                                 regressionRslt.alpha,
                                 Math.sqrt( regressionRslt.alphaVariance ),
                                 regressionRslt.beta,
                                 Math.sqrt( regressionRslt.betaVariance ),
                                 Math.sqrt( regressionRslt.residualVariance ) );
            }
        }

        return makeSpannableString( reportSW.toString(), textSpans );
    }
    
//=============================================================================

    // private static final String LOGTAG = "CorrelationActivity";

//-----------------------------------------------------------------------------
}                                                         //CorrelationActivity


//*****************************************************************************
