/*
  StatsActivity.java
  Copyright © 2010 David M. Anderson

  Abstract base class for the statistical analysis activities in SimpleStats.
*/

package us.EpsilonDelta.SimpleStats;

import us.EpsilonDelta.math.Stats;
import java.util.List;
import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.util.DisplayMetrics;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
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
        LayoutInflater inflater = getLayoutInflater( );
        m_layout = inflater.inflate( getLayoutId(), null );
        fixupTextViews( (ViewGroup)m_layout );
        setContentView( m_layout );
    }

//-----------------------------------------------------------------------------

    @Override
    public
    void
    onResume( )
    {
        super.onResume( );
        setupAnalyzeButton( );
        setupHypothRelationSpinner( );
        m_layout.requestLayout( );
    }

//-----------------------------------------------------------------------------

    private
    void
    setupAnalyzeButton( )
    {
        Button analyzeButton = (Button)findViewById( R.id.analyze );
        if ( analyzeButton != null )
            analyzeButton.setOnClickListener( this );
    }

//-----------------------------------------------------------------------------

    private
    void
    setupHypothRelationSpinner( )
    {
        Spinner h1Spinner
                = (Spinner)findViewById( R.id.H1RelationSpinner );
        if ( h1Spinner != null )
        {
            List< String > h1RelSymbols = new ArrayList< String >( );
            for ( Stats.Tail tail : ms_tails )
                h1RelSymbols.add( tail.h1RelationSymbol );
            ArrayAdapter< String > spinnerAdapter
                    = new ArrayAdapter< String >( getApplicationContext(),
                                           android.R.layout.simple_spinner_item,
                                           h1RelSymbols );
            spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item );
            h1Spinner.setAdapter( spinnerAdapter );
            h1Spinner.setSelection( m_tailIndex, true );
            h1Spinner.setOnItemSelectedListener(
                new HypothRelationSpinnerListener() );
        }
    }

//-----------------------------------------------------------------------------

    private
    void
    fixupTextViews( ViewGroup viewGroup )
    {
        if ( viewGroup == null )
            return;
        for ( int i = 0; i < viewGroup.getChildCount(); ++i )
        {
            View child = viewGroup.getChildAt( i );
            if ( child instanceof ViewGroup )
                fixupTextViews( (ViewGroup)child );
            else if ( child instanceof TextView )
            {
                TextView textView = (TextView)child;
                CharSequence text = textView.getText( );
                SpannableString ss = makeSpannableString( text.toString() );
                textView.setText( ss );
                textView.invalidate( );
            }
        }
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

    protected
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
        
        public final int start;
        public final int end;
        public final CharacterStyle style;
    }

//-----------------------------------------------------------------------------

    protected static
    SpannableString
    makeSpannableString( String text, List< TextSpan > textSpans )
    {
        text = fixupSubscripts( text, textSpans );
        
        SpannableString ss = new SpannableString( text );
        for ( TextSpan ts : textSpans )
        {
            ss.setSpan( ts.style, ts.start, ts.end, 0 );
        }
        return ss;
    }
    
//.............................................................................

    protected static
    SpannableString
    makeSpannableString( String text )
    {
        return makeSpannableString( text, new ArrayList< TextSpan >() );
    }
    
//-----------------------------------------------------------------------------
    
    private static
    String
    fixupSubscripts( String text, List< TextSpan > textSpans )
    {
        //Android font currently is missing ₀ (x2080) character,
        // so we'll replace it with 0. (Subscripting is possible, but
        // turns out to be tricky.)
        //For a consistent appearance, we'll also replace ₁ (x2081) in H₁.
        return text.replaceAll( "₀", "0" ).replaceAll( "H₁", "H1" );
    }


//=============================================================================

    
    private
    class
    HypothRelationSpinnerListener
        implements OnItemSelectedListener
    {                                           //HypothRelationSpinnerListener
    //-------------------------------------------------------------------------

        public
        void
        onItemSelected( AdapterView<?> parent,
                        View view, int index, long id )
        {
            m_tailIndex = index;
            m_tail = ms_tails[ index ];
            TextView h0RelText
                    = (TextView)findViewById( R.id.H0Relation );
            if ( h0RelText != null )
            {
                h0RelText.setText( m_tail.h0RelationSymbol );
            }
        }
        
    //-------------------------------------------------------------------------

        public
        void
        onNothingSelected( AdapterView<?> parent )
        {
            //Do nothing
        }
        
    //-------------------------------------------------------------------------
    }                                           //HypothRelationSpinnerListener

    
//=============================================================================

    
    protected Stats.Tail m_tail = Stats.Tail.BOTH;
    
//.............................................................................

    View m_layout = null;
    private static Stats.Tail[ ] ms_tails
        = { Stats.Tail.BOTH, Stats.Tail.LOWER, Stats.Tail.UPPER };
    private int m_tailIndex = 0;
    // private static final String LOGTAG = "StatsActivity";

//-----------------------------------------------------------------------------
}                                                               //StatsActivity


//*****************************************************************************
