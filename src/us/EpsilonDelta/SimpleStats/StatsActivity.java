/*
  StatsActivity.java
  Copyright © 2010 David M. Anderson

  Abstract base class for the statistical analysis activities in SimpleStats.
*/

package us.EpsilonDelta.SimpleStats;

import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
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
        if ( (savedInstanceState != null)
             && (savedInstanceState.getBoolean( "ShowingAnalysis", false )) )
            m_showAnalysis = true;
        LayoutInflater layoutInflater = getLayoutInflater();
        int layoutId = getLayoutId();
        m_layout = layoutInflater.inflate( layoutId, null );
        setContentView( m_layout );
    }

//-----------------------------------------------------------------------------

    @Override
    public
    void
    onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
        outState.putBoolean( "ShowingAnalysis", m_showingAnalysis );
    }

//-----------------------------------------------------------------------------

    @Override
    public
    void
    onResume( )
    {
        super.onResume( );
        ((Button)findViewById( R.id.analyze )).setOnClickListener( this );
        if ( m_showAnalysis )
        {
            m_showAnalysis = false;
            showDialog( ANALYSIS_DIALOG_ID );
        }
    }

//=============================================================================

    public
    void
    onClick( View v )
    {
        showDialog( ANALYSIS_DIALOG_ID );
    }

//=============================================================================

    @Override
    protected
    Dialog
    onCreateDialog( int dialogId )
    {
        switch ( dialogId )
        {
        case ANALYSIS_DIALOG_ID:
        {
            AlertDialog.Builder builder = new AlertDialog.Builder( this );

            LayoutInflater layoutInflater = getLayoutInflater();
            View layout
                    = layoutInflater.inflate( R.layout.analysis_dialog, null );
            builder.setView( layout );
            DialogInterface.OnClickListener clickListener
                    = new DialogInterface.OnClickListener()
                        {
                            public void
                            onClick( DialogInterface dialog, int buttonId )
                            {
                                dialog.dismiss( );
                            }
                        };
            DialogInterface.OnDismissListener dismissListener
                    = new DialogInterface.OnDismissListener()
                        {
                            public void
                            onDismiss( DialogInterface dialog )
                            {
                                m_showingAnalysis = false;
                            }
                        };

            builder.setPositiveButton( R.string.OK, clickListener );
            Dialog dialog = builder.create( );
            dialog.setOnDismissListener( dismissListener );
            return dialog;
        }
        default:
            return null;
        }
    }

//-----------------------------------------------------------------------------

    @Override
    protected
    void
    onPrepareDialog( int dialogId, Dialog dialog )
    {
        super.onPrepareDialog( dialogId, dialog );
        switch ( dialogId )
        {
        case ANALYSIS_DIALOG_ID:
        {
            m_showingAnalysis = true;
            CharSequence analysisText = getAnalysisText( );
            ((TextView)dialog.findViewById( R.id.analysis_text ))
                    .setText( analysisText );
            break;
        }
        }
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

    private static final int ANALYSIS_DIALOG_ID = 1;
    private View m_layout;
    private boolean m_showAnalysis = false;
    private boolean m_showingAnalysis = false;
    // private static final String LOGTAG = "StatsActivity";

//-----------------------------------------------------------------------------
}                                                               //StatsActivity


//*****************************************************************************
