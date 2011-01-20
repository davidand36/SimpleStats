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
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.text.SpannableString;
// import android.util.Log;


//*****************************************************************************


public
abstract
class StatsActivity
    extends Activity
{                                                               //StatsActivity
//-----------------------------------------------------------------------------

    protected abstract int getLayoutId( );

    protected abstract CharSequence getAnalysisText( );

    protected abstract int getHelpTextId( );
    
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
            analyzeButton.setOnClickListener( new AnalyzeButtonListener() );
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
                SpannableString ss
                        = StringUtil.makeSpannableString( text.toString() );
                textView.setText( ss );
                textView.invalidate( );
            }
        }
    }
    
//=============================================================================

    @Override
    public
    boolean
    onCreateOptionsMenu( Menu menu )
    {
        super.onCreateOptionsMenu( menu );
        getMenuInflater().inflate( R.menu.options, menu );

        Intent intent = new Intent( getApplicationContext(),
                                    TextActivity.class );
        intent.putExtra( "us.EpsilonDelta.SimpleStats.LAYOUT_ID",
                         R.layout.plain_text );
        intent.putExtra( "us.EpsilonDelta.SimpleStats.TEXT_ID",
                         getHelpTextId() );
        menu.findItem( R.id.help_menu_item ).setIntent( intent );

        intent = new Intent( getApplicationContext(), TextActivity.class );
        intent.putExtra( "us.EpsilonDelta.SimpleStats.LAYOUT_ID",
                         R.layout.plain_text );
        intent.putExtra( "us.EpsilonDelta.SimpleStats.TEXT_ID",
                         R.string.hypotheses_help_text );
        menu.findItem( R.id.hypotheses_menu_item ).setIntent( intent );

        intent = new Intent( getApplicationContext(), TextActivity.class );
        intent.putExtra( "us.EpsilonDelta.SimpleStats.LAYOUT_ID",
                         R.layout.about );
        intent.putExtra( "us.EpsilonDelta.SimpleStats.TEXT_ID",
                         R.string.about_help_text );
        menu.findItem( R.id.about_menu_item ).setIntent( intent );

        return true;
    }

//-----------------------------------------------------------------------------

    @Override
    public
    boolean
    onOptionsItemSelected( MenuItem item )
    {
        super.onOptionsItemSelected( item );
        startActivity( item.getIntent() );
        return true;
    }
    
//=============================================================================

    protected
    boolean
    isPortrait( )
    {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

//=============================================================================


    private
    class
    AnalyzeButtonListener
        implements OnClickListener
    {                                                   //AnalyzeButtonListener
    //-------------------------------------------------------------------------

        public
        void
        onClick( View v )
        {
            CharSequence analysisText = getAnalysisText( );
            Intent intent = new Intent( getApplicationContext(),
                                        TextActivity.class );
            intent.putExtra( "us.EpsilonDelta.SimpleStats.LAYOUT_ID",
                             R.layout.plain_text );
            intent.putExtra( "us.EpsilonDelta.SimpleStats.TEXT", analysisText );
            startActivity( intent );
        }

    //-------------------------------------------------------------------------
    }                                                   //AnalyzeButtonListener


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

    private View m_layout = null;
    private static Stats.Tail[ ] ms_tails
        = { Stats.Tail.BOTH, Stats.Tail.LOWER, Stats.Tail.UPPER };
    private int m_tailIndex = 0;
    // private static final String LOGTAG = "StatsActivity";

//-----------------------------------------------------------------------------
}                                                               //StatsActivity


//*****************************************************************************
