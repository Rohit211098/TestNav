package dev.rohitrawat.testnav

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity

abstract class AddStickerPackActivity : ComponentActivity() {

    private  val ADD_PACK = 200
    protected fun addStickerPackToWhatsApp(identifier: String, stickerPackName: String , packageManager: PackageManager) {
        try {
            //if neither WhatsApp Consumer or WhatsApp Business is installed, then tell user to install the apps.
            if (!WhitelistCheck.isWhatsAppConsumerAppInstalled(packageManager) && !WhitelistCheck.isWhatsAppSmbAppInstalled(
                    packageManager
                )
            ) {
                Toast.makeText(
                    applicationContext,
                    R.string.add_pack_fail_prompt_update_whatsapp,
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            val stickerPackWhitelistedInWhatsAppConsumer: Boolean =
                WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(this, identifier)
            val stickerPackWhitelistedInWhatsAppSmb: Boolean =
                WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(this, identifier)
            if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
                //ask users which app to add the pack to.
                launchIntentToAddPackToChooser(identifier, stickerPackName)
            } else if (!stickerPackWhitelistedInWhatsAppConsumer) {
                launchIntentToAddPackToSpecificPackage(
                    identifier,
                    stickerPackName,
                    WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME
                )
            } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                launchIntentToAddPackToSpecificPackage(
                    identifier,
                    stickerPackName,
                    WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME
                )
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.add_pack_fail_prompt_update_whatsapp,
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Log.e("TAG", "error adding sticker pack to WhatsApp", e)
            Toast.makeText(applicationContext, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun launchIntentToAddPackToSpecificPackage(
        identifier: String,
        stickerPackName: String,
        whatsappPackageName: String
    ) {
        val intent = createIntentToAddStickerPack(identifier, stickerPackName)
        intent.setPackage(whatsappPackageName)
        try {
            startActivityForResult(intent, ADD_PACK)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG)
                .show()
        }
    }

    //Handle cases either of WhatsApp are set as default app to handle this intent. We still want users to see both options.
    private fun launchIntentToAddPackToChooser(identifier: String, stickerPackName: String) {
        val intent = createIntentToAddStickerPack(identifier, stickerPackName)
        try {
            startActivityForResult(
                Intent.createChooser(
                    intent,
                    getString(R.string.add_to_whatsapp)
                ), ADD_PACK
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun createIntentToAddStickerPack(identifier: String, stickerPackName: String): Intent {
        val intent = Intent()
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK")
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_ID, identifier)
        intent.putExtra(
            StickerPackDetailsActivity.EXTRA_STICKER_PACK_AUTHORITY,
            BuildConfig.CONTENT_PROVIDER_AUTHORITY
        )
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_NAME, stickerPackName)
        return intent
    }

//    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == ADD_PACK) {
//            if (resultCode == Activity.RESULT_CANCELED) {
//                if (data != null) {
//                    val validationError = data.getStringExtra("validation_error")
//                    if (validationError != null) {
//                        if (BuildConfig.DEBUG) {
//                            //validation error should be shown to developer only, not users.
//                            MessageDialogFragment.newInstance(
//                                R.string.title_validation_error,
//                                validationError
//                            ).show(getSupportFragmentManager(), "validation error")
//                        }
//                        Log.e(
//                            TAG,
//                            "Validation failed:$validationError"
//                        )
//                    }
//                } else {
//                    StickerPackNotAddedMessageFragment().show(
//                        getSupportFragmentManager(),
//                        "sticker_pack_not_added"
//                    )
//                }
//            }
//        }
//    }

//    class StickerPackNotAddedMessageFragment : DialogFragment() {
//        fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//            val dialogBuilder: AlertDialog.Builder = Builder(getActivity())
//                .setMessage(R.string.add_pack_fail_prompt_update_whatsapp)
//                .setCancelable(true)
//                .setPositiveButton(android.R.string.ok) { dialog, which -> dismiss() }
//                .setNeutralButton(R.string.add_pack_fail_prompt_update_play_link) { dialog, which -> launchWhatsAppPlayStorePage() }
//            return dialogBuilder.create()
//        }

//        private fun launchWhatsAppPlayStorePage() {
//            if (getActivity() != null) {
//                val packageManager: PackageManager = getActivity().getPackageManager()
//                val whatsAppInstalled: Boolean = WhitelistCheck.isPackageInstalled(
//                    WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME,
//                    packageManager
//                )
//                val smbAppInstalled: Boolean = WhitelistCheck.isPackageInstalled(
//                    WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME,
//                    packageManager
//                )
//                val playPackageLinkPrefix = "http://play.google.com/store/apps/details?id="
//                if (whatsAppInstalled && smbAppInstalled) {
//                    launchPlayStoreWithUri("https://play.google.com/store/apps/developer?id=WhatsApp+LLC")
//                } else if (whatsAppInstalled) {
//                    launchPlayStoreWithUri(playPackageLinkPrefix + WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME)
//                } else if (smbAppInstalled) {
//                    launchPlayStoreWithUri(playPackageLinkPrefix + WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME)
//                }
//            }
//        }

//        private fun launchPlayStoreWithUri(uriString: String) {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.setData(Uri.parse(uriString))
//            intent.setPackage("com.android.vending")
//            try {
//                startActivity(intent)
//            } catch (e: ActivityNotFoundException) {
//                Toast.makeText(getActivity(), R.string.cannot_find_play_store, Toast.LENGTH_LONG)
//                    .show()
//            }
//        }
    }

//    companion object {
//        private const val ADD_PACK = 200
//        private const val TAG = "AddStickerPackActivity"
//    }

//}

