/*
  MeansActivity.java
  Copyright © 2010 David M. Anderson

  Activity for input and analysis of numeric samples from two populations.
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
class MeansActivity
    extends StatsActivity
{                                                               //MeansActivity
//-----------------------------------------------------------------------------

    @Override
    protected
    int
    getLayoutId( )
    {
        return R.layout.means;
    }

//=============================================================================

    @Override
    protected
    CharSequence
    getAnalysisText( )
    {
        Sample[] samples = new Sample[ NUM_SAMPLES ];
        String sampleText
                = ((EditText)findViewById( R.id.means_sample1Data ))
                .getText().toString();
        samples[ 0 ] = new Sample( sampleText );
        sampleText = ((EditText)findViewById( R.id.means_sample2Data ))
                .getText().toString();
        samples[ 1 ] = new Sample( sampleText );
        int precision = 0;
        for ( Sample s : samples )
            precision = Math.max( precision, s.precision() );
        CharSequence analysisText
                = writeAnalysisText( samples, precision );
        return analysisText;
    }
    
//-----------------------------------------------------------------------------
    
    private
    CharSequence
    writeAnalysisText( Sample[] samples, int precision )
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
        String twoTailed_ = getResources().getString( R.string.twoTailed );
        String assuming_ = getResources().getString( R.string.assuming );
        String dof_ = getResources().getString( R.string.dof );
        String dofs_ = getResources().getString( R.string.dofs );
        String Probability_ = getResources().getString( R.string.Probability );

        //Extra line ends and spaces if in portrait orientation:
        String lend0 = (isPortrait()  ?  "\n"  :  "");
        String lend1 = (isPortrait()  ?  "\n "  :  "");
        
        for ( int i = 0; i < samples.length; ++i )
        {
            Sample sample = samples[ i ];
            if ( i > 0 )
                reportPW.printf( "\n" );
            int start = reportSW.toString().length();
            
            reportPW.printf( Sample_ + " %d", (i + 1) );

            int end = reportSW.toString().length();
            textSpans.add( new TextSpan( start, end,
                                         new StyleSpan( Typeface.ITALIC ) ) );

            reportPW.printf( " " + size_ + "=%d", sample.size() );
            if ( sample.size() > 0 )
            {
                reportPW.printf( "\n"
                                 + "  " + Min_ + "=" + statFmt0 + lend1
                                 + " " + Max_ + "=" + statFmt0 + lend1
                                 + " " + Median_ + "=" + statFmt1 + "\n"
                                 + "  " + Mean_ + "=",
                                 sample.min(), sample.max(), sample.median() );
                start = reportSW.toString().length();

                reportPW.printf( statFmt2, sample.mean() );

                end = reportSW.toString().length();
                textSpans.add( new TextSpan( start, end,
                                             new StyleSpan( Typeface.BOLD ) ) );
            
                if ( sample.size() > 1 )
                {
                    reportPW.printf( lend1 + " " + StdDev_ + "=" + statFmt2,
                                     Math.sqrt( sample.variance() ) );
                }
            }
        }

        if ( (samples[ 0 ].size() > 1) && (samples[ 1 ].size() > 1) )
        {

            Stats.TTestResult meansEqVResult
                    = Stats.meansTest( samples[ 0 ].size(), samples[ 0 ].mean(),
                                       samples[ 0 ].variance(),
                                       samples[ 1 ].size(), samples[ 1 ].mean(),
                                       samples[ 1 ].variance(),
                                       true, 1 );
            Stats.TTestResult meansNoneqVResult
                    = Stats.meansTest( samples[ 0 ].size(), samples[ 0 ].mean(),
                                       samples[ 0 ].variance(),
                                       samples[ 1 ].size(), samples[ 1 ].mean(),
                                       samples[ 1 ].variance(),
                                       false, 1 );
            Stats.FTestResult varsResult
                    = Stats.variancesTest( samples[ 0 ].size(),
                                           samples[ 0 ].variance(),
                                           samples[ 1 ].size(),
                                           samples[ 1 ].variance(), 2 );
            boolean variancesEqual
                    = (varsResult.probability > EQUAL_VAR_CUTOFF);
            reportPW.printf( "\n\n"
                             + TestOfH0_ + " (µ₁=µ₂)," + lend0
                             + " " + assuming_ + " σ²₁=σ²₂:\n"
                             + "  t=%.3f (" + dof_ + "=%d)" + lend1
                             + " " + Probability_
                             + " (" + oneTailed_ + ") = ",
                             meansEqVResult.t,
                             meansEqVResult.degreesOfFreedom );
            int start = reportSW.toString().length();
            reportPW.printf( "%.3f", meansEqVResult.probability );
            int end = reportSW.toString().length();
            if ( variancesEqual )
                textSpans.add( new TextSpan( start, end,
                                             new StyleSpan( Typeface.BOLD ) ) );
                
            reportPW.printf( "\n"
                             + TestOfH0_ + " (µ₁=µ₂):\n"
                             + "  t=%.3f (" + dof_ + "=%d)" + lend1
                             + " " + Probability_
                             + " (" + oneTailed_ + ") = ",
                             meansNoneqVResult.t,
                             meansNoneqVResult.degreesOfFreedom );
            start = reportSW.toString().length();
            reportPW.printf( "%.3f", meansNoneqVResult.probability );
            end = reportSW.toString().length();
            if ( ! variancesEqual )
                textSpans.add( new TextSpan( start, end,
                                             new StyleSpan( Typeface.BOLD ) ) );
                
            reportPW.printf( "\n"
                             + TestOfH0_ + " (σ²₁=σ²₂):\n"
                             + "  F=%.3f (" + dofs_ + "=%d, %d)" + lend1
                             + " " + Probability_
                             + " (" + twoTailed_ + ") = "
                             + "%.3f",
                             varsResult.f,
                             varsResult.degreesOfFreedom1,
                             varsResult.degreesOfFreedom2,
                             varsResult.probability );
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

    private static final int NUM_SAMPLES = 2;
    private static final double EQUAL_VAR_CUTOFF = 0.10;
    // private static final String LOGTAG = "MeansActivity";

//-----------------------------------------------------------------------------
}                                                               //MeansActivity


//*****************************************************************************
