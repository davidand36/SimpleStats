/*
  SimpleStats.java
  Copyright © 2010 David M. Anderson

  Main activity for SimpleStats Android app.
*/

package us.EpsilonDelta.SimpleStats;

import android.app.TabActivity;
import android.os.Bundle;
import android.content.Context;
import android.widget.TabHost;
import android.graphics.drawable.Drawable;
import android.content.Intent;
// import android.util.Log;


//*****************************************************************************


public class SimpleStatsActivity 
	extends TabActivity 
{                                                         //SimpleStatsActivity
//-----------------------------------------------------------------------------

    @Override
    public
    void
    onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate(savedInstanceState);
        final Context appContext = getApplicationContext();
        
        final TabHost tabHost = getTabHost();
        
        String tabTitle = getResources().getString( R.string.meanTitle );
        Drawable tabIcon = getResources().getDrawable( R.drawable.mu_tab_icon );
        Intent intent = new Intent( appContext, MeanActivity.class );
        tabHost.addTab( tabHost.newTabSpec( "meanTab" )
                        .setIndicator( tabTitle, tabIcon )
                        .setContent( intent ) );

        tabTitle = getResources().getString( R.string.meansTitle );
        tabIcon = getResources().getDrawable( R.drawable.mu2_tab_icon );
        intent = new Intent( appContext, MeansActivity.class );
        tabHost.addTab( tabHost.newTabSpec( "meansTab" )
                        .setIndicator( tabTitle, tabIcon )
                        .setContent( intent ) );

        tabTitle = getResources().getString( R.string.correlationTitle );
        tabIcon = getResources().getDrawable( R.drawable.rho_tab_icon );
        intent = new Intent( appContext, CorrelationActivity.class );
        tabHost.addTab( tabHost.newTabSpec( "correlationTab" )
                        .setIndicator( tabTitle, tabIcon )
                        .setContent( intent ) );

        tabTitle = getResources().getString( R.string.contingencyTitle );
        tabIcon = getResources().getDrawable( R.drawable.chi2_tab_icon );
        intent = new Intent( appContext, ContingencyActivity.class );
        tabHost.addTab( tabHost.newTabSpec( "contingencyTab" )
                        .setIndicator( tabTitle, tabIcon )
                        .setContent( intent ) );
    }

//=============================================================================

    // private static final String LOGTAG = "SimpleStatsActivity";
    
//-----------------------------------------------------------------------------
}                                                         //SimpleStatsActivity


//*****************************************************************************
