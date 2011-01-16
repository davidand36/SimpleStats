/*
  TextActivity.java
  Copyright © 2010 David M. Anderson

  Activity that displays text but offers no interactivity.
*/

package us.EpsilonDelta.SimpleStats;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;


//*****************************************************************************


public
class TextActivity
    extends Activity
{                                                                //TextActivity
//-----------------------------------------------------------------------------

    @Override
    public
    void
    onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        Intent intent = getIntent();
        CharSequence title = intent.getCharSequenceExtra(
            "us.EpsilonDelta.SimpleStats.TITLE" );
        if ( title != null )
        {
            setTitle( title );
        }
        int layoutId = intent.getIntExtra(
            "us.EpsilonDelta.SimpleStats.LAYOUT_ID", 0 );
        setContentView( layoutId );
        CharSequence text = "";
        int textId = intent.getIntExtra(
            "us.EpsilonDelta.SimpleStats.TEXT_ID", 0 );
        if ( textId != 0 )
        {
            String rawText = getResources().getString( textId );
            text = StringUtil.makeSpannableString( rawText );
        }
        else
        {
            text = intent.getCharSequenceExtra(
                "us.EpsilonDelta.SimpleStats.TEXT" );
        }
        ((TextView)findViewById( R.id.text )).setText( text );
    }

//-----------------------------------------------------------------------------
}                                                                //TextActivity


//*****************************************************************************
