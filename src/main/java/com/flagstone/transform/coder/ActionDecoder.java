/*
 * ActionDecoder.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.coder;

import com.flagstone.transform.action.Action;
import com.flagstone.transform.action.ActionObject;
import com.flagstone.transform.action.ActionTypes;
import com.flagstone.transform.action.BasicAction;
import com.flagstone.transform.action.Call;
import com.flagstone.transform.action.ExceptionHandler;
import com.flagstone.transform.action.GetUrl;
import com.flagstone.transform.action.GetUrl2;
import com.flagstone.transform.action.GotoFrame;
import com.flagstone.transform.action.GotoFrame2;
import com.flagstone.transform.action.GotoLabel;
import com.flagstone.transform.action.If;
import com.flagstone.transform.action.Jump;
import com.flagstone.transform.action.NewFunction;
import com.flagstone.transform.action.NewFunction2;
import com.flagstone.transform.action.Push;
import com.flagstone.transform.action.RegisterCopy;
import com.flagstone.transform.action.SetTarget;
import com.flagstone.transform.action.Table;
import com.flagstone.transform.action.WaitForFrame;
import com.flagstone.transform.action.WaitForFrame2;
import com.flagstone.transform.action.With;

/**
 * ActionDecoder decodes the actions in the Flash movie.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class ActionDecoder implements SWFFactory<Action> {

    /** {@inheritDoc} */
    public Action getObject(final SWFDecoder coder, final Context context)
            throws CoderException {

        Action action;

        final int type = coder.scanByte();

        if (type <= ActionTypes.HIGHEST_BYTE_CODE) {
            action = BasicAction.fromInt(coder.readByte());
        } else {
            switch (type) {
            case ActionTypes.GET_URL:
                action = new GetUrl(coder);
                break;
            case ActionTypes.GOTO_FRAME:
                action = new GotoFrame(coder);
                break;
            case ActionTypes.GOTO_LABEL:
                action = new GotoLabel(coder);
                break;
            case ActionTypes.SET_TARGET:
                action = new SetTarget(coder);
                break;
            case ActionTypes.WAIT_FOR_FRAME:
                action = new WaitForFrame(coder);
                break;
            case ActionTypes.CALL:
                action = Call.getInstance();
                coder.adjustPointer(24);
                break;
            case ActionTypes.PUSH:
                action = new Push(coder);
                break;
            case ActionTypes.WAIT_FOR_FRAME_2:
                action = new WaitForFrame2(coder);
                break;
            case ActionTypes.JUMP:
                action = new Jump(coder);
                break;
            case ActionTypes.IF:
                action = new If(coder);
                break;
            case ActionTypes.GET_URL_2:
                action = new GetUrl2(coder);
                break;
            case ActionTypes.GOTO_FRAME_2:
                action = new GotoFrame2(coder);
                break;
            case ActionTypes.TABLE:
                action = new Table(coder);
                break;
            case ActionTypes.REGISTER_COPY:
                action = new RegisterCopy(coder);
                break;
            case ActionTypes.NEW_FUNCTION:
                action = new NewFunction(coder, context);
                break;
            case ActionTypes.WITH:
                action = new With(coder, context);
                break;
            case ActionTypes.EXCEPTION_HANDLER:
                action = new ExceptionHandler(coder, context);
                break;
            case ActionTypes.NEW_FUNCTION_2:
                action = new NewFunction2(coder, context);
                break;
            default:
                action = new ActionObject(coder);
                break;
            }
        }
        return action;
    }

}
