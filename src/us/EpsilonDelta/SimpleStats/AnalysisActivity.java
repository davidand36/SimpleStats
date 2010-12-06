/*
  AnalysisActivity.java
  Copyright © 2010 David M. Anderson

  Activity that displays the data analysis results.
*/

package us.EpsilonDelta.SimpleStats;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;


//*****************************************************************************


public
class AnalysisActivity
    extends Activity
{                                                            //AnalysisActivity
//-----------------------------------------------------------------------------

    @Override
    public
    void
    onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.analysis );
        Intent intent = getIntent();
        CharSequence analysisText = intent.getCharSequenceExtra(
            "us.EpsilonDelta.SimpleStats.ANALYSIS_TEXT" );
        ((TextView)findViewById( R.id.analysis_text )).setText( analysisText );
    }

//-----------------------------------------------------------------------------
}                                                            //AnalysisActivity


//*****************************************************************************
