Continuations library by Matthias Mann from <http://www.matthiasmann.de/content/view/24/26/>. Original documentation follows.

# Continuations Library by Matthias Mann

This small library allows you to write your game AI or animation code in a simple sequential way, blocking on commands like `walkTo()` and all this without blocking your AI thread or the need for additional threads.

Some of you might be familiar with the features of scripting languages like [Lua](http://www.lua.org/) or [AngelScript](http://www.angelcode.com/angelscript/). One of these features are Coroutines. These allow a control transfer that is beyond a simple `if`/`else` or `goto`. Somewhere you define the entry point - the coroutine - and while executing inside the coroutine you can suspend your code and return to the coroutine call. This is comparable to stack unwinding of exceptions. But coroutines offer you the unique feature to return to the state where you suspended execution. This allows you to write code in a simple sequential way that would otherwise require either its own thread or a hand written state machine.

But I'm writing this for the Java rubric - and this feature is not part of your standard Java runtime today. This library offers you a very easy and still power full way to use these features in Java without affecting your code by intrusive requirements.

Lets start with what you get:

* Write simple sequential code - you no longer need to create state machines by hand
* No `Thread`s are created or needed - no multi thread synchronization issues
* No garbage creation from code execution
* Very small runtime overhead
* Only suspendable method calls are changed - all calls into your standard library (like `java.util.*` etc) are not affected at all.
* Full serialization support
* You can store the execution state of coroutines as part of your game state in your save game without any additional code. This of course requires that your classes and data types which you use in your coroutines are serializable.
* Full support for exception handling and finally blocks
* Offline preprocessing does not slow down you application load time
* Of course runtime instrumentation is also possible.
* Very small runtime library - less then 10 KByte (uncompressed JAR)
* BSD License

With all these great features - you may be asking for the drawbacks. Well there are of course a few drawbacks:

* Constructors and static initializers can't be suspended
* Suspendable methods can't be synchronized or have synchronized blocks
* You need to download [ASM3 library](https://asm.ow2.io/) to run the instrumentation task 

You can't call suspendable method with reflection

The synchronization issue can be worked around by putting code which requires the use of synchronization into its own method.

Lets look at an example - an iterator. Writing iterators can be quite complex - especially if the implementation is more then just a table walk. This library comes with a utility class that allows you to write iterators in a simple sequential way - also known as producers. It is based on a JUnit test which is also distributed with the source code of this library.

```java
public class TestIterator extends CoIterator<String> implements Serializable {
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
```

This class extends the class `CoIterator` which provides the `produce` method and implements all methods of the `Iterator` interface.
The `run` method is an abstract method from `CoIterator` which is the body of our iterator. This method is declared to throw the special exception `SuspendExecution`. This exception must never be caught by any user code - it must be always propagated. But don't fear - you can still use the infamous `catch(Throwable)` if you like. This exception is the marker for suspendable methods - it tells the preprocessor that this method calls others methods which might suspend execution. In our example each call to `produce` will suspend execution until the consumer (the caller of `next`) has consumed the produced value. Let's take a look at code that uses our `Iterator`:

```java
Iterator<String> iter1 = new TestIterator();
while(iter.hasNext()) {
    System.out.println(iter.next());
}
```

Which will produce the following output:

```
A
B
C0
C1
C2
C3
D
E
```

This looks very familiar - it's your everyday iterator usage. Of course it also works with an enhanced `for` loop if the `Iterator` is returned from a class which implements `Iterable`.

But you can also write this with standard Java code without support from this library:

```java
public class TestIterator implements Iterator<String>, Serializable {
    private int state;
    private int i;
    public String next() {
        switch(state) {
            case 0: state=1; return "A";
            case 1: state=2; i=0; return "B";
            case 2:
              if(i == 3) state = 3;
              return "C" + (i++);
            case 3: state=4; return "D";
            case 4: state=5; return "E";
            default: throw new NoSuchElementException();
        }
    }
    public boolean hasNext() {
        return state < 5;
    }
    public void remove() {
        throw new UnsupportedOperationException("Not supported");
    }
}
```

That is much more complex - and if your code gets more complicated it will be very hard to create a state machine for it. But with the help of this library you can spend your time to write your application.

An important point is that the underlying `Coroutine` is only executed when `hasNext()` or `next()` of the `Iterator` is executed. `hasNext()` will return `false` if the `run()` method finishes its execution. It also means that if you don't consume all values that the `Iterator` returns that the `run()` method of your `CoIterator` subclass never get the chance to finish its execution - which includes any not yet executed finally blocks.

If you look closely at our `TestIterator` class you will see that it also implements `Serializable` which means that you can store the iterator in a file. See the JUnit test for the complete example.

To use the Ant task you need the full JAR and the full ASM3 JAR. It also works with ASM 3.1. All dependencies of your application needs to be in the class path of the instrumentation Ant task - otherwise it can't analyze the method calls. You can safely run this task over already instrumented class files - it will detect already transformed classes and skip them. This example Ant snippet assumes the Netbeans build properties:

```xml
<taskdef name="continuations"
    classname="de.matthiasmann.continuations.instrument.InstrumentationTask"
    classpath="Continuations_full.jar:asm-all-3.0.jar:${run.classpath}"/>
 
<target name="-post-compile">
    <continuations verbose="true">
        <fileset dir="${build.classes.dir}"/>
    </continuations>
</target>
```

The embedded fileset is used to specify the list of classes that should be instrumented. The task also has a few parameters that can be used to alter its behavior:

|Attribute|Description|Required|Default|
|---------|-----------|--------|-------|
|`verbose`|Outputs informations about each processing step|No|`false`|
|`check`|Includes the ASM verifier in the transformation chain. Mostly a debugging option.|No|`false`|
|`debug`|Outputs internal and detailed information about the processing of the class files.|No|`false`|

Enough with the talk for now - here is the link to the library:

* Javadoc (multiple HTML pages)
* Binaries, Source code and zipped Javadoc
* ASM3 (for the 2010 version), ASM4 (for the 2012 and newer version) library

So have fun and let me know if you find it useful