package org.kreal.biliconvert.setting

import android.os.Bundle
import android.view.MenuItem

class SettingsActivity : BaseSlideCloseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (fragmentManager.findFragmentById(android.R.id.content) == null)
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, SettingsFragment())
                    .commit()
        setupActionBar()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
