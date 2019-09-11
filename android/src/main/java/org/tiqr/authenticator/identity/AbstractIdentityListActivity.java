package org.tiqr.authenticator.identity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import org.tiqr.authenticator.R;
import org.tiqr.authenticator.datamodel.DbAdapter;
import org.tiqr.authenticator.security.SecretStore;

public abstract class AbstractIdentityListActivity extends ListActivity {
    protected static final int REQUEST_BASE = 0;


    protected static String[] FROM = { DbAdapter.Companion.getIDENTIFIER(), DbAdapter.Companion.getDISPLAY_NAME() };
    protected static int[] TO = {R.id.identifier, R.id.displayName};

    private Cursor _identitiesCursor;

    protected DbAdapter _db;
    protected int _resourceid = R.layout.identity_listitem;

    private void showIdentities() {
        IdentityCursorAdapter adapter = new IdentityCursorAdapter(this, _resourceid, getIdentityCursor(), FROM, TO);
        setListAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identity);

        _db = new DbAdapter(this);
        try {
            new SecretStore(this);
        } catch (Exception ex) {
            Log.e(AbstractIdentityListActivity.class.getName(), "Initializing the keystore has failed!", ex);
        }

        startManagingCursor(getIdentityCursor());

        showIdentities();
    }

    protected Cursor _createIdentityCursor() {
        return _db.getAllIdentitiesWithIdentityProviderData();
    }

    public Cursor getIdentityCursor() {
        if (_identitiesCursor == null) {
            _identitiesCursor = _createIdentityCursor();
        }
        return _identitiesCursor;
    }


    /**
     * Handle the result of the sub activity.
     *
     * @param requestCode
     * @param resultCode
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
    }
}
