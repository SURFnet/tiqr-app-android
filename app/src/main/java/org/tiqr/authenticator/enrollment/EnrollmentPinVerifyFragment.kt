/*
 * Copyright (c) 2010-2020 SURFnet bv
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of SURFnet bv nor the names of its contributors
 *    may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.tiqr.authenticator.enrollment

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import org.tiqr.authenticator.R
import org.tiqr.authenticator.base.BindingFragment
import org.tiqr.authenticator.databinding.FragmentEnrollmentPinVerifyBinding
import org.tiqr.data.model.ChallengeCompleteResult
import org.tiqr.data.viewmodel.EnrollmentViewModel
import timber.log.Timber

/**
 * Fragment to confirm the PIN for the enrollment
 */
class EnrollmentPinVerifyFragment : BindingFragment<FragmentEnrollmentPinVerifyBinding>() {
    private val viewModel by navGraphViewModels<EnrollmentViewModel>(R.id.enrollment_nav) { factory }
    private val args by navArgs<EnrollmentPinVerifyFragmentArgs>()

    @LayoutRes
    override val layout = R.layout.fragment_enrollment_pin_verify

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = binding ?: return
        binding.pin.setConfirmListener { pin ->
            if (pin != args.pin) {
                AlertDialog.Builder(requireContext())
                        .setTitle(R.string.enroll_pin_verify_no_match_title)
                        .setMessage(R.string.enroll_pin_verify_no_match_message)
                        .setCancelable(false)
                        .setNegativeButton(R.string.button_cancel) { _, _ -> findNavController().popBackStack() }
                        .setPositiveButton(R.string.button_retry) { dialog, _ ->
                            binding.pin.clear()
                            dialog.dismiss()
                        }
                        .show()
            } else {
                viewModel.enroll(pin)

                // TODO: show upgrade PIN to biometric
            }
        }

        viewModel.enroll.observe(viewLifecycleOwner) {
            when(it) {
                ChallengeCompleteResult.Success -> {
                    findNavController().navigate(EnrollmentPinVerifyFragmentDirections.actionSummary())
                }
                is ChallengeCompleteResult.Failure -> {
                    AlertDialog.Builder(requireContext())
                            .setTitle(it.failure.title)
                            .setMessage(it.failure.message)
                            .show()
                }
            }
        }
    }
}