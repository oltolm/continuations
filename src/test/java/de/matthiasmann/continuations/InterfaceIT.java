/*
 * Copyright (c) 2008-2013, Matthias Mann
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Matthias Mann nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
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
package de.matthiasmann.continuations;

import org.junit.Test;

/**
 *
 * @author Elias Naur
 */
public class InterfaceIT {
 
	public class C2 implements SomeInterface {
		public void doStuff() throws SuspendExecution {
		}
	}
 
	public class C implements SomeInterface {
		public void doStuff() throws SuspendExecution {
/*			float time = 0f;
			float seconds = .8f;
			do {
				float t = .06667f;
				System.out.println("time = " + time + " " + (time + t));
				//          time = StrictMath.min(time + t, seconds);
				time = time + t;
				System.out.println("seconds = " + seconds + " | time = " + time + " | t = " + t);
				System.out.println("this = " + this);
 
				System.out.println("time just not after = " + time);
				Coroutine.yield();
				System.out.println("time after = " + time);
			} while (time < seconds);
			System.out.println("1 = " + 1);*/
		}
	}
 
    @Test
    public void testSuspend() {
//		final I i = new C();
        Coroutine co = new Coroutine(new CoroutineProto() {
			public final void coExecute() throws SuspendExecution {
                                // next line causes an error because of incomplete merge in TypeInterpreter
				//SomeInterface i = System.currentTimeMillis() > 0 ? new C() : new C2();
                                SomeInterface i = new C();
System.out.println("i = " + i);
				i.doStuff();
			}
		});
        while(co.getState() != Coroutine.State.FINISHED) {
            System.out.println("State="+co.getState());
            co.run();
        }
        System.out.println("State="+co.getState());
    }
 
}