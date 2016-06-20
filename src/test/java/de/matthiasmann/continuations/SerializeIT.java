/*
 * Copyright (c) 2008, Matthias Mann
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import org.junit.Test;

/**
 *
 * @author Matthias Mann
 */
public class SerializeIT {

    @Test
    public void testSerialize() throws IOException, ClassNotFoundException, SuspendExecution {
        Iterator<String> iter1 = new TestIterator();

        assertEquals("A", iter1.next());
        assertEquals("B", iter1.next());
        assertEquals("C0", iter1.next());
        assertEquals("C1", iter1.next());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(iter1);
        oos.close();

        byte[] bytes = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        @SuppressWarnings("unchecked")
        Iterator<String> iter2 = (Iterator<String>)ois.readObject();

        assertNotSame(iter1, iter2);

        assertEquals("C2", iter2.next());
        assertEquals("C3", iter2.next());
        assertEquals("D", iter2.next());
        assertEquals("E", iter2.next());
        assertFalse(iter2.hasNext());

        assertEquals("C2", iter1.next());
        assertEquals("C3", iter1.next());
        assertEquals("D", iter1.next());
        assertEquals("E", iter1.next());
        assertFalse(iter1.hasNext());
    }

    private static class TestIterator extends CoIterator<String> {
        @Override
        protected void run() throws SuspendExecution {
            produce("A");
            produce("B");
            for(int i = 0; i < 4; i++) {
                produce("C" + i);
            }
            produce("D");
            produce("E");
        }
    }
}
