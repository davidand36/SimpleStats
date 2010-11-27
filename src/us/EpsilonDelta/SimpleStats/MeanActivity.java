/*
  MeanActivity.java
  Copyright © 2010 David M. Anderson

  Activity for input and analysis of a single population numeric sample.
*/

package us.EpsilonDelta.SimpleStats;

import us.EpsilonDelta.math.Stats;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import android.widget.EditText;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
// import android.util.Log;


//*****************************************************************************


public
class MeanActivity
    extends StatsActivity
{                                                                //MeanActivity
//-----------------------------------------------------------------------------

    @Override
    protected
    int
    getLayoutId( )
    {
        return R.layout.mean;
    }

//=============================================================================

    @Override
    protected
    CharSequence
    getAnalysisText( )
    {
        String sampleText = ((EditText)findViewById( R.id.mean_sampleData ))
                .getText().toString();
        Sample sample = new Sample( sampleText );
        int precision = sample.precision();
        String hypothMeanText
                = ((EditText)findViewById( R.id.mean_hypothMean ))
                .getText().toString();
        double hypothMean = 0.;
        try
        {
            hypothMean = Double.parseDouble( hypothMeanText );
            precision = Math.max( precision,
                                  NumberParser.getPrecision( hypothMeanText ) );
        }
        catch ( NumberFormatException e ) { } //ignore
        CharSequence analysisText
                = writeAnalysisText( sample, hypothMean, precision );
        return analysisText;
    }
    
//-----------------------------------------------------------------------------
    
    private
    CharSequence
    writeAnalysisText( Sample sample, double hypothMean, int precision )
    {
        StringWriter reportSW = new StringWriter( );
        PrintWriter reportPW = new PrintWriter( reportSW );
        List< TextSpan > textSpans = new ArrayList< TextSpan >( );
        String statFmt0 = "%." + precision + "f";
        String statFmt1 = "%." + (precision + 1) + "f";
        String statFmt2 = "%." + (precision + 2) + "f";

        String Sample_ = getResources().getString( R.string.Sample );
        String size_ = getResources().getString( R.string.size );
        String Min_ = getResources().getString( R.string.Min );
        String Max_ = getResources().getString( R.string.Max );
        String Median_ = getResources().getString( R.string.Median );
        String Mean_ = getResources().getString( R.string.Mean );
        String StdDev_ = getResources().getString( R.string.StdDev );
        String TestOfH0_ = getResources().getString( R.string.TestOfH0 );
        String oneTailed_ = getResources().getString( R.string.oneTailed );
        String dof_ = getResources().getString( R.string.dof );
        String Probability_ = getResources().getString( R.string.Probability );

        //Extra line ends and spaces if in portrait orientation:
        String lend1 = (isPortrait()  ?  "\n "  :  "");
        
        reportPW.printf( Sample_ + " " + size_ + "=%d", sample.size() );
        if ( sample.size() > 0 )
        {
            reportPW.printf( "\n"
                             + "  " + Min_ + "=" + statFmt0 + lend1
                             + " " + Max_ + "=" + statFmt0 + lend1
                             + " " + Median_ + "=" + statFmt1 + "\n"
                             + "  " + Mean_ + "=",
                             sample.min(), sample.max(), sample.median() );
            int start = reportSW.toString().length();

            reportPW.printf( statFmt2, sample.mean() );

            int end = reportSW.toString().length();
            textSpans.add( new TextSpan( start, end,
                                         new StyleSpan( Typeface.BOLD ) ) );
            
            if ( sample.size() > 1 )
            {
                reportPW.printf( lend1 + " " + StdDev_ + "=" + statFmt2,
                                 Math.sqrt( sample.variance() ) );

                Stats.TTestResult result
                        = Stats.meanTest( sample.size(), sample.mean(),
                                          sample.variance(), hypothMean, 1 );
                reportPW.printf( "\n\n"
                                 + TestOfH0_ + " (µ=" + statFmt0 + "):\n"
                                 + "  t=%.3f (" + dof_ + "=%d)" + lend1
                                 + " " + Probability_
                                 + " (" + oneTailed_ + ") = ",
                                 hypothMean,
                                 result.t,
                                 result.degreesOfFreedom );
                start = reportSW.toString().length();

                reportPW.printf(  "%.3f", result.probability );

                end = reportSW.toString().length();
                textSpans.add( new TextSpan( start, end,
                                             new StyleSpan( Typeface.BOLD ) ) );
            }
        }

        String reportString = reportSW.toString();
        fixupH0s( reportString, textSpans );
        
        SpannableString analysisText
                = new SpannableString( reportString );
        for ( TextSpan ts : textSpans )
        {
            analysisText.setSpan( ts.style, ts.start, ts.end, 0 );
        }

        return analysisText;
    }
    
//=============================================================================

//    private static final String LOGTAG = "MeanActivity";

//-----------------------------------------------------------------------------
}                                                                //MeanActivity


//*****************************************************************************
