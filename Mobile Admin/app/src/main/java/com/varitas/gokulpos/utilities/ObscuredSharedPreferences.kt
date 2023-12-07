package com.varitas.gokulpos.utilities

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener

class ObscuredSharedPreferences(private val delegate: SharedPreferences, password: String?) : SharedPreferences {
    private val crypt: CryptoManager = CryptoManager(password!!)
    override fun edit(): Editor {
        return Editor()
    }

    override fun getAll(): Map<String, *> {
        throw UnsupportedOperationException("This method is not implemented in " + ObscuredSharedPreferences::class.java.simpleName) // left as an exercise to the reader
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        val v = delegate.getString(crypt.encrypt(key), null)
        return if (v != null) java.lang.Boolean.parseBoolean(crypt.decrypt(v)) else defValue
    }

    override fun getFloat(key: String, defValue: Float): Float {
        val v = delegate.getString(crypt.encrypt(key), null)
        return if (v != null) crypt.decrypt(v).toFloat() else defValue
    }

    override fun getInt(key: String, defValue: Int): Int {
        val v = delegate.getString(crypt.encrypt(key), null)
        return if (v != null) crypt.decrypt(v).toInt() else defValue
    }

    override fun getLong(key: String, defValue: Long): Long {
        val v = delegate.getString(crypt.encrypt(key), null)
        return if (v != null) crypt.decrypt(v).toLong() else defValue
    }

    override fun getString(key: String, defValue: String?): String? {
        val v = delegate.getString(crypt.encrypt(key), null)
        return if (v != null) crypt.decrypt(v) else defValue
    }

    override fun contains(s: String): Boolean {
        return delegate.contains(crypt.encrypt(s))
    }

    override fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener) {
        delegate.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener) {
        delegate.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun getStringSet(arg0: String, arg1: Set<String>?): Set<String>? {
        throw UnsupportedOperationException("This method is not implemented in " + ObscuredSharedPreferences::class.java.simpleName)
    }

    open inner class Editor : SharedPreferences.Editor {
        private var delegate: SharedPreferences.Editor = this@ObscuredSharedPreferences.delegate.edit()
        override fun putBoolean(key: String, value: Boolean): Editor {
            delegate.putString(crypt.encrypt(key), crypt.encrypt(java.lang.Boolean.toString(value)))
            return this
        }

        override fun putFloat(key: String, value: Float): Editor {
            delegate.putString(crypt.encrypt(key), crypt.encrypt(value.toString()))
            return this
        }

        override fun putInt(key: String, value: Int): Editor {
            delegate.putString(crypt.encrypt(key), crypt.encrypt(value.toString()))
            return this
        }

        override fun putLong(key: String, value: Long): Editor {
            delegate.putString(crypt.encrypt(key), crypt.encrypt(value.toString()))
            return this
        }

        override fun putString(key: String, value: String?): Editor {
            delegate.putString(crypt.encrypt(key), crypt.encrypt(value))
            return this
        }

        override fun apply() {
            delegate.apply()
        }

        override fun clear(): Editor {
            delegate.clear()
            return this
        }

        override fun commit(): Boolean {
            return delegate.commit()
        }

        override fun remove(key: String): Editor {
            delegate.remove(crypt.encrypt(key))
            return this
        }

        override fun putStringSet(arg0: String, arg1: Set<String>?): SharedPreferences.Editor? {
            return null
        }
    }
}