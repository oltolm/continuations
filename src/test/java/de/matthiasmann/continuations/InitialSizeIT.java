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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;

/**
 *
 * @author Matthias Mann
 */
public class InitialSizeIT implements CoroutineProto {

    @Test
    public void test1() {
        testWithSize(1);
    }

    @Test
    public void test2() {
        testWithSize(2);
    }

    @Test
    public void test3() {
        testWithSize(3);
    }

    private void testWithSize(int stackSize) {
        Coroutine c = new Coroutine(this, stackSize);
        assertEquals(getStackSize(c), stackSize);
        c.run();
        assertEquals(Coroutine.State.SUSPENDED, c.getState());
        c.run();
        assertEquals(Coroutine.State.FINISHED, c.getState());
        assertTrue(getStackSize(c) > 10);
    }

    @Override
    public void coExecute() throws SuspendExecution {
        assertEquals(3628800, factorial(10));
    }

    private int factorial(Integer a) throws SuspendExecution {
        if(a == 0) {
            Coroutine.yield();
            return 1;
        }
        return a * factorial(a - 1);
    }

    private int getStackSize(Coroutine c) {
        try {
            Field stackField = Coroutine.class.getDeclaredField("stack");
            stackField.setAccessible(true);
            Object stack = stackField.get(c);
            Field dataObjectField = Stack.class.getDeclaredField("dataObject");
            dataObjectField.setAccessible(true);
            Object[] dataObject = (Object[])dataObjectField.get(stack);
            return dataObject.length;
        } catch(Throwable ex) {
            throw new AssertionError(ex);
        }
    }
}
