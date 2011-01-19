/*
  StringUtil.java
  Copyright © 2011 David M. Anderson

  StringUtil class, offering TextSpan class and methods for transforming text.
*/

package us.EpsilonDelta.SimpleStats;

import java.util.List;
import java.util.ArrayList;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.SubscriptSpan;


//*****************************************************************************


public
class StringUtil
{                                                                  //StringUtil
//-----------------------------------------------------------------------------

    public static
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

    public static
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
        // so we'll replace it with 0.
        //For a consistent appearance, we'll also replace ₁ (x2081) in H₁.
        String newText = text.replaceAll( "₀", "0" ).replaceAll( "H₁", "H1" );

        int h0Loc = -1;
        do
        {
            h0Loc = newText.indexOf( "H0", h0Loc );
            if ( h0Loc >= 0 )
            {
                textSpans.add( new TextSpan( h0Loc + 1, h0Loc + 2,
                                             new SubscriptSpan( ) ) );
                h0Loc += 2;
            }
        } while ( h0Loc >= 0 );
        int h1Loc = -1;
        do
        {
            h1Loc = newText.indexOf( "H1", h1Loc );
            if ( h1Loc >= 0 )
            {
                textSpans.add( new TextSpan( h1Loc + 1, h1Loc + 2,
                                             new SubscriptSpan( ) ) );
                h1Loc += 2;
            }
        } while ( h1Loc >= 0 );

        return newText;
    }

//=============================================================================

    
    public static
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
}                                                                  //StringUtil


//*****************************************************************************
