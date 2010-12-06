/*
  StatsActivity.java
  Copyright © 2010 David M. Anderson

  Abstract base class for the statistical analysis activities in SimpleStats.
*/

package us.EpsilonDelta.SimpleStats;

import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.util.DisplayMetrics;
import android.text.style.CharacterStyle;
import android.text.style.SubscriptSpan;
// import android.util.Log;


//*****************************************************************************


public
abstract
class StatsActivity
    extends Activity
    implements OnClickListener
{                                                               //StatsActivity
//-----------------------------------------------------------------------------

    protected abstract int getLayoutId( );

    protected abstract CharSequence getAnalysisText( );

//=============================================================================

    @Override
    public
    void
    onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( getLayoutId() );
    }

//-----------------------------------------------------------------------------

    @Override
    public
    void
    onResume( )
    {
        super.onResume( );
        ((Button)findViewById( R.id.analyze )).setOnClickListener( this );
    }

//=============================================================================

    public
    void
    onClick( View v )
    {
        CharSequence analysisText = getAnalysisText( );
        Intent intent = new Intent( getApplicationContext(),
                                    AnalysisActivity.class );
        intent.putExtra( "us.EpsilonDelta.SimpleStats.ANALYSIS_TEXT",
                         analysisText );
        startActivity( intent );
    }

//=============================================================================

    public
    boolean
    isPortrait( )
    {
        DisplayMetrics metrics = new DisplayMetrics( );
        getWindowManager().getDefaultDisplay().getMetrics( metrics );
        return (metrics.widthPixels <= metrics.heightPixels);
    }
    
//=============================================================================
    
    protected static
    class TextSpan
    {
        TextSpan( int start, int end, CharacterStyle style )
        {
            this.start = start;
            this.end = end;
            this.style = style;
        }
        
        public int start;
        public int end;
        public CharacterStyle style;
    }

//=============================================================================

    protected static
    void
    fixupH0s( String text, List< TextSpan > textSpans )
    {
        //Android font currently is missing subscript-0 ('₀') character,
        // so we'll subscript the '0' in "H0" in our analysis strings.
        int h0Loc = 0;
        do
        {
            h0Loc = text.indexOf( "H0", h0Loc );
            if ( h0Loc >= 0 )
            {
                textSpans.add( new TextSpan( h0Loc + 1, h0Loc + 2,
                                             new SubscriptSpan( ) ) );
                h0Loc += 2;
            }
        } while ( h0Loc >= 0 );
    }
    
//=============================================================================

    // private static final String LOGTAG = "StatsActivity";

//-----------------------------------------------------------------------------
}                                                               //StatsActivity


//*****************************************************************************
