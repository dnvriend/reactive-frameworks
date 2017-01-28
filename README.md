# reactive-frameworks
A small study project on the capabilities of reactive frameworks like [spring-boot](http://projects.spring.io/spring-boot/),
the [Lagomframework](http://www.lagomframework.com/) and the [Playframework](https://www.playframework.com/).

## TL;DR
When dealing with state-of-the-art microservice components we are talking about high performance computing
and low latency throughputs. These kinds of systems are called reactive systems and need fundamental
design principles in where these reactive traits are intrinsicly present in every little part of the system, from the
IO driver to the computational model. When compared to traditional JEE-based frameworks this means a shift in both
the computational paradigm which means functional and composable and the communication models which means
asynchronous messaging combined with asynchronous back-pressured event-streams in the form of a unified log.

Being intrinsicly reactive in every aspect of the design does __not__ mean small little changes in the current framework design
like with Servlet 3.0 and Servlet 3.1 and call it a day. Most of the time it means either a complete redesign or carefully
picking the right tooling for the job.

Reactive Systems do sequential evaluation of expressions which are mostly cpu bounded and asynchronous message passing which is
mostly IO bounded. These kinds of systems needs a hybrid approach of both threaded and evented and that is what [Netty](http://netty.io/)
in combination with the Actor model in the form of [Akka](http://akka.io/) paired with [Scala](http://www.scala-lang.org/)
a high performance, strongly typed, object-functional programming language that supports asynchronous computations in the form
of Scala Futures that executes computations on highly tuned thread pools requiring only a minmum amount of threads, provide.
These separate components combined into a platform provide a unique execution model never before seen to the JVM and are
a requirement for building reactive systems which are high performance, low latency computing systems and are available
in the form of both the [Playframework](https://www.playframework.com/) and [Lagomframework](http://www.lagomframework.com/).

For the unified log [Apache Kafka](http://kafka.apache.org/) is the de-facto component of choice and the system is paired
with [Apache Cassandra](http://cassandra.apache.org/) for storage of aggegated data that has been crunched by
[Apache Spark](http://spark.apache.org/). The system also needs a computing clustering management platform in the form of
[Apache Mesos](http://mesos.apache.org/) combined with [Lightbend ConductR](https://conductr.lightbend.com/) to schedule
and configure the reactive components.

## Reactive Systems - a Recap
Systems built as Reactive Systems are more flexible, loosely-coupled and scalable.
These traits make reactive systems easier to develop and amenable to change.
Reactive systems are significantly more tolerant of failure and when failure does occur, and failure will occur!
Reactive systems meet failure with elegance rather than disaster.
Reactive Systems are highly responsive, giving users effective interactive feedback.

Reactive Systems are:

- Responsive: this is the whole reason for architecting, designing and developing reactive systems:
  - The system responds in a timely manner if at all possible.
  - Responsiveness is the cornerstone of usability and utility, but more than that, responsiveness means that problems may be detected quickly and dealt with effectively.
  - Responsive systems focus on providing rapid and consistent response times, establishing reliable upper bounds so they deliver a consistent quality of service.
  - This consistent behaviour in turn simplifies error handling, builds end user confidence, and encourages further interaction.
  - The entire system resolves the user queries quickly due to the selection of the most performant query models that could be designed; aggregates in the write model are completely independent and easy to cache, which improves command resolution performance; the event store is highly optimized for append-only operations and fetching events for a single aggregate.

- Resilient: Reliability, Fault Tolerance, Recovery
  - The system stays responsive in the face of failure.
  - This applies not only to highly-available, mission critical systems — any system that is not resilient will be unresponsive after a failure.
  - Resilience is achieved by replication, containment, isolation and delegation.
  - Failures are contained within each component, isolating components from each other and thereby ensuring that parts of the system can fail and recover without compromising the system as a whole.
  - Recovery of each component is delegated to another (external) component and high-availability is ensured by replication where necessary.
  - The client of a component is not burdened with handling its failures.
  - For example, Akka provides supervisor-based failure management to reliably handle failure.
  – losing an instance of a write or read model does not prevent the system from working and the lost instance may be easily recreated; the event store uses datastores with enabled replication,

- Elastic/Scalable:
  - The system stays responsive under varying workload.
  - Reactive Systems can react to changes in the input rate by increasing or decreasing the resources allocated to service these inputs.
  - This implies designs that have no contention points or central bottlenecks, resulting in the ability to shard or replicate components and distribute inputs among them.
  - Reactive Systems support predictive, as well as Reactive, scaling algorithms by providing relevant live performance measures.
  - They achieve elasticity in a cost-effective way on commodity hardware and software platforms.
  - adding new write and read model instances results in better performance; the event store is built on top of scalable datastores,
  - it's scalable thanks to Akka’s configuration and ultimately to the Akka Cluster,
  - Increase on horizontal scalability with help from frameworks that keeps immutability and the stateless paradigm at its core design and principles.

- Message Driven:
  - Reactive Systems rely on asynchronous message-passing to establish a boundary between components that ensures loose coupling, isolation and location transparency.
  - This boundary also provides the means to delegate failures as messages.
  - Employing explicit message-passing enables load management, elasticity, and flow control by shaping and monitoring the message queues in the system and applying back-pressure when necessary.
  - Location transparent messaging as a means of communication makes it possible for the management of failure to work with the same constructs and semantics across a cluster or within a single host.
  - Non-blocking communication allows recipients to only consume resources while active, leading to less system overhead.
  – commands trigger events, events trigger model updates,
  - it’s completely asynchronous since it leverages Akka for everything.
  - Separation between state and thread pool in Akka eliminates memory leaks
  - Immutable data processing excludes locking completely

Large systems are composed of smaller ones and therefore depend on the Reactive properties of their constituents.
This means that Reactive Systems apply design principles so these properties apply at all levels of scale, making them composable.
The largest systems in the world rely upon architectures based on these properties and serve the needs of billions of people daily.
It is time to apply these design principles consciously from the start instead of rediscovering them each time.

These traits we will look for in the frameworks.

## HTTP 1.0 to HTTP 1.1
A major improvement in the HTTP 1.1 standard is persistent connections. In HTTP 1.0, a connection between a Web client
and server is closed after a single request/response cycle. In HTTP 1.1, a connection is kept alive and reused for multiple
requests. Persistent connections reduce communication lag perceptibly, because the client doesn't need to renegotiate
the TCP connection after each request.

## Thread per connection
Figuring out how to make Web servers more scalable is an ongoing challenge for vendors. Thread per HTTP connection,
which is based on HTTP 1.1's persistent connections, is a common solution vendors have adopted. Under this strategy,
each HTTP connection between client and server is associated with one thread on the server side. Threads are allocated
from a server-managed thread pool. Once a connection is closed, the dedicated thread is recycled back to the pool and
is ready to serve other tasks. Depending on the hardware configuration, this approach can scale to a high number
of concurrent connections. Experiments with high-profile Web servers have yielded numerical results revealing
that memory consumption increases almost in direct proportion with the number of HTTP connections. The reason is that
threads are relatively expensive in terms of memory use. Servers configured with a fixed number of threads can suffer
the thread starvation problem, whereby requests from new clients are rejected once all the threads in the pool are taken.

On the other hand, for many Web sites, users request pages from the server only sporadically. This is known as a page-by-page model.
The connection threads are idling most of the time, which is a waste of resources.

## Thread per request
Thanks to the non-blocking I/O capability introduced in Java 4's New I/O APIs for the Java Platform (NIO) package,
a persistent HTTP connection doesn't require that a thread be constantly attached to it. Threads can be allocated to
connections only when requests are being processed. When a connection is idle between requests, the thread can be recycled,
and the connection is placed in a centralized NIO select set to detect new requests without consuming a separate thread.
This model, called 'thread per request', potentially allows Web servers to handle a growing number of user connections
with a fixed number of threads. With the same hardware configuration, Web servers running in this mode scale much better
than in the 'thread-per-connection' mode. Today, popular Web servers, including Tomcat, Jetty, GlassFish (Grizzly), WebLogic,
and WebSphere all use thread per request through Java NIO. For application developers, the good news is that Web servers
implement non-blocking I/O in a hidden manner, with no exposure whatsoever to applications through servlet APIs.

## Threaded servers
Most servers are threaded, which means 'one-thread-per-request' and mostly use blocking IO, of course the newer JEE standard
like servlet 3.0 have introduced a callback alternative, which makes it possible to not block on the request handling thread
and register a callback that will complete the request (the DeferredResult) but that needs another thread pool, you have to submit
work to that thread, and you have to figure out what kind of work you want to submit/schedule like for example computational
work or (blocking) IO work which makes the properties of the thread pool different. All in all these 'extensions' to the
servlet containers and servlet standard ie. thread-per-request servers do not make these server architectures intrinsicly non-blocking.

To recap: it is possible to schedule asychronously, but like any concurrency subject on the JVM using Java, it is very ackward
to design and to execute and very error prone.

Examples of threaded servers are:

- [Apache Tomcat](http://tomcat.apache.org/)
- [Jetty](https://eclipse.org/jetty/)
- [Glassfish](https://glassfish.java.net/)
- [JBoss AS](http://jbossas.jboss.org/)
- [Wildfly](http://wildfly.org/)
- [IBM Websphere](http://www-03.ibm.com/software/products/en/appserv-was)

## Evented servers
Evented servers have one thread/process per CPU core and use non-blocking IO. They require that all operations
are non-blocking. When for some reason a subsystem does block like for example when writing to a serial device
like a file system or a (blocking) database, than these systems suffer from serious performance issues so non
blocking computations and IO are a must!

Examples of evented servers are:

- [Node.js](https://nodejs.org/en/)
- [Netty](http://netty.io/)
- [Akka](http://akka.io/)
- [Undertow](http://undertow.io/)
- [Project Reactor](https://projectreactor.io/)

## Hybrid Threaded/Evented servers
These frameworks are hybrid because they support the combination of sequential evaluation of expressions which is mostly
cpu bounded and asynchronous message passing which is mostly IO bounded by means of the asynchronous threaded design
of Scala Futures, the evented design of Netty and can leverage asynchronous messaging and supervision model of the Akka
toolkit. This hybrid design of being both threaded, evented and message driven is what the reactive components that
Lightbend designs, develops and supports bring to the table in the form of two very developer friendly frameworks
Lagomframework and the Playframework, so we will add Play and Lagom to our list.

Examples of hybrid Threaded/Evented platforms are:

- [Playframework](https://www.playframework.com/): An open source web application framework optimized for high performance computing and a building block for creating reactive systems that provides a developer friendy, quick turnaround developing and deployment model which is unique in its kind,
- [Lagomframework](http://www.lagomframework.com/): An open source microservice framework that builds on playframework but provides an opinionated best practices way for building reactive systems using the microservice architectural style that provides a develoer friendly, quick turnaround for developing and deploying reactive systems using the microservice architectural style, is unique in its kind,

## Threaded vs Evented
Broadly speaking, there are two ways to handle concurrent requests to a server. Threaded servers use multiple concurrently-executing
threads that each handle one client request, while evented servers run a single event loop that handles events for all connected clients.

The discussion is, threaded vs evented, why does it matter? Well, like the reactive manifesto states, large systems are
composed of smaller ones and therefore depend on the (reactive) properties of their constituents, this means that the system
as a whole relies on the properties of the smallest components which it is composed of. Technically this means that systems
are composed of services and because the system is composed of services that as a coherent group are responsible for
executing a problem domain they spend a lot of time sending messages to each other.

When using a threaded model for service composition in where communication is key and a lot of communication will happen,
a thread-per-request blocking model equals to poor node resource utilization. In effect it would mean a lot of waiting, and
a lot of context switching. Of course most of the time the problem lies with the (blocking) data stores and the (unnecessary)
strict consistency models across entities that we as developers tend to choose.

Why is waiting bad? Well, it depends on what the service is doing. Actually the web site [Latency Numbers Every Programmer Should Know](https://people.eecs.berkeley.edu/~rcs/research/interactive_latency.html)
shows the latency per year depending on what the computation is and even in 2017 it still means that IO has the most influence on latency on a system and guess what
services do between them, a lot of IO. Computation wise, like for example traversing a list is many times faster than doing a (simple)
IO operation, so it is key to not blocking when doing IO.

When having a system composition of many services that are doing thread-per-request and maybe (some of) those services doing some kind of
ackward asynchronous callback-style 'non-blocking-IO' by just spawning new threads on a separate thread pool still
causes a congestion of those threads on the separate thread pool. All these thread wait times add up.
This means that at one time most of your threads are still waiting on IO and the server still suffers from thread-starvation and
requests time out.

You could say, just increase the number of threads on your IO scheduling thread pool, but the question that arises is, what
is the correct size of the pool? If you make it too big then you have a lot of overhead like memory, context switching, etc.
If you make it too small then the server will suffer from thread starvation and you get request timeouts.

This story is just for one service of course, but because large systems are composed of small services, for a threaded service
that depends on each other this means that a change in the wait time in one service means longer wait times in another service and
even longer wait times on the edge servers and because messages are asynchonous and non-deterministic (which is good),
before you know it the whole system stops working (which is bad).

We can conclude that the problem lies not just with Threaded vs Evented but with the IO. When the workload is only CPU bound
so for example only traversing a list then the Threaded and the Evented variants most likely will perform the same.

One thing to note is for Evented servers to really shine the following must be true:

- all external resources must be accessed through non-blocking drivers
- an evented architecture becomes more viable when the ratio wall-time to cpu-time increases, so when waiting for external resources
  like IO then the evented architecture vs threaded becomes more viable

Final thing to note is for the Playframework, it is a multithreaded and evented server that means that it performs
really well when used with non-blocking IO only, but also performes well when blocking IO is used. The thread pools in
play can be used for different performance profiles.

## Threaded vs. Evented performance
Threads can have significant memory overhead (e.g. default stack size for a single thread is 1MB on a 64bit JVM)
and context switching overhead (e.g. saving register state, loading register state, impact on CPU cache/pipeline, lock contention).
Creating threads on the fly tends to be expensive, so most servers use a fixed thread pool.

Therefore, with Threaded servers, the name of the game is "sizing the thread pool". If you don't have enough threads,
it’s easy for all of them to become tied up waiting for I/O, preventing any new requests from being processed even though
most of your threads are just idly waiting. If you have too many threads, the extra memory usage and context switching overhead
become very costly. Ideally, you want the thread pool to have as many threads as the maximum number of concurrent calls
your server will handle. Unfortunately, this number changes as traffic and/or downstream latency increases.

In other words, you're trying to configure a statically sized thread pool, but the proper value depends on something very
dynamic (latency and traffic). Worse yet, you need to configure this thread pool on hundreds of different services,
each of which talk to each other, impact each other's latency and traffic, and therefore, affect the very thread pool
values you're trying to pick! This is thread pool hell.

On Evented servers, waiting for I/O is very cheap: idle requests have negligible cost, as they don’t hold up a thread.
This is important, because waiting on I/O will typically dwarf all other server operations: for example, check out
Latency Numbers Every Programmer Should Know and compare the time for a single roundtrip in a data center (500,000ns)
to any operation within the same server (~5ns).

This means that for typical workloads, Evented servers will be able to handle far more concurrent requests than Threaded servers.
However, be warned: even a single long calculation or accidental blocking I/O call can bring an Evented server to its knees.
Check out [Threaded vs. Evented Servers](http://mmcgrana.github.io/2010/07/threaded-vs-evented-servers.html) for some
simple mathematical models of how threaded and evented servers behave under different load profiles.

## Threaded vs. Evented programming models
In most real-world scenarios, you’ll have to make several I/O calls, and to make them fast, you’ll need to do them in parallel.

In the Threaded world, performing I/O in parallel usually involves setting up a separate thread pool (more thread pool hell!).
However, that means your I/O is now executing asynchronously, so, just as in the Evented world, you now need a way to say
"execute this code later". At this point, you either switch to an Evented programming style, or you bolt on some solution
that feels Threaded, such as Java Futures (with blocking get() calls) or continuations. In either case, complexity goes up
and flexibility goes down.

In the Evented world, all I/O runs in parallel by default and "execute this code later" is a core concept that will feel
natural and consistent.

## The Hybrid Threaded/Evented model
Being purely threaded or being purely evented goes so far in real world scenarios. The sweet spot lies in the middle, a system
should be evented but also should be threaded and the system should optimally scale up or down depending on the current load
that the system experiences. Hybrid systems like the [Playframework](https://www.playframework.com/) and [Lagomframework](http://www.lagomframework.com/)
do just that. Based on eventing the system can schedule work to be done and when a thread is ready a job can be executed on a thread,
and those thread pools are elastic. This hybrid design could only be created by completly redesigning the execution model that
until now traditionally was provided by JEE servers or JEE compliant platforms like Spring.

Examples of hybrid approaches are:

- [Playframework](https://www.playframework.com/): An open source web application framework optimized for high performance computing and a building block for creating reactive systems that provides a developer friendy, quick turnaround developing and deployment model which is unique in its kind,
- [Lagomframework](http://www.lagomframework.com/): An open source microservice framework that builds on playframework but provides an opinionated best practices way for building reactive systems using the microservice architectural style that provides a develoer friendly, quick turnaround for developing and deploying reactive systems using the microservice architectural style, is unique in its kind,

## When the hybrid model is not enough
Being hybrid is not enough in reactive systems. Just receiving messages and queueing these request waiting for a thread to be available
can only get you so far. The system-as-a-whole should respond to each other when one system for some reason starts queueing up requests.
For this reason a separate communication stream that communicates demand is defined where first demand is requested by the downstream
component and the upstream component supplies that much messaging as the demand can handle. Communicating demand this way makes for a
very dynamic system and the effect is called back-pressure and should of course be done in a non-blocking way.

The [Reactive Streams Specification](http://www.reactive-streams.org/) provides a standard for asynchronous stream processing with
non-blocking back pressure that defines the just described mechanism. The [Playframework](https://www.playframework.com/) and
[Lagomframework](http://www.lagomframework.com/) provide the reactive streams machanism.

## Reactive Streams
Reactive Streams is an initiative to provide a standard for __asynchronous stream processing__ with non-blocking back pressure.
This encompasses efforts aimed at runtime environments (JVM and JavaScript) as well as network protocols.

Handling streams of data—especially 'live' data whose volume is not predetermined—requires special care in an asynchronous system.
The most prominent issue is that resource consumption needs to be controlled such that a fast data source does not overwhelm the
stream destination. Asynchrony is needed in order to enable the parallel use of computing resources, on collaborating network
hosts or multiple CPU cores within a single machine.

The main goal of Reactive Streams is to govern the exchange of stream data across an asynchronous boundary—think passing
elements on to another thread or thread-pool—while ensuring that the receiving side is not forced to buffer arbitrary
amounts of data. In other words, back pressure is an integral part of this model in order to allow the queues which mediate
between threads to be bounded. The benefits of asynchronous processing would be negated if the communication of
back pressure were synchronous, therefore care has to be taken to mandate fully non-blocking and asynchronous behavior
of all aspects of a Reactive Streams implementation.

It is the intention of this specification to allow the creation of many conforming implementations, which by virtue of abiding
by the rules will be able to interoperate smoothly, preserving the aforementioned benefits and characteristics across the
whole processing graph of a stream application.

Reactive Streams started as an initiative in late 2013 between engineers at Netflix, Pivotal and Typesafe (now called 'Lightbend').
Some of the earliest discussions began in 2013 between the Play and Akka teams at Typesafe. Typesafe is one of the main contributors
of Reactive Streams. Other contributors include Red Hat, Oracle, Twitter and spray.io. Work is under way to make the Java
implementation of Reactive Streams part of Java 9, Doug Lea, leader of JSR 166, has proposed a new Flow class that will
include the interfaces currently provided by Reactive Streams.

Some reactive streams compliant implementations that are relevant to the discussion are:

- [Akka Streams](http://doc.akka.io/docs/akka/2.4/scala/stream/index.html): Akka streams provides basic building blocks and components in the form of the [Alpakka](https://github.com/akka/alpakka) project to build complex processing graphs on the JVM based on the Reactive Streams Specification.
- [Project Reactor](https://projectreactor.io/): Reactor is a second-generation Reactive library for building non-blocking applications on the JVM based on the Reactive Streams Specification.

## Reactive Systems and Spring
Spring MVC is the standard way for building microservices which means that you'll need a server that supports the Servlet
standard and that also means that the execution model that comes out of the box when you use spring (boot) is thread-based, based on blocking
IO APIs and based on a high level of mutability.

These traits are not the best to build components that other components will come to rely on, as previously discussed in the
Threaded vs Evented discussion. Of course in the future there will be some areas where the __reactive streams__ compliant component
can play a part for creating back-pressured event-driven processing pipelines. As of Januari 2017 the libraries are not yet
production ready and not yet production proven.

Building reactive systems means recognising which parts of the system is message based and which part of the system is stream based.
When it comes to Spring, the way to do inter-service communication is REST and that is implemented by means of Spring-MVC which means
that you'll need a Servlet based server which means a threaded blocking model.

Having so many blocking APIs at your disposal is a real risk when choosing to use spring-boot. When reading up on the subject
I got the feeling that there are indeed some APIs that behave in a non-blocking fashion but it is pick-and-choose wisely.

I can safely conclude that spring-boot is not yet ready to be called 'reactive' but I will admit that there is a lot of choice
and functionality to quickly build systems out of, but I woudn't call those systems 'reactive' by any stretch of the imagination.

I copied the following verbatim from the [following article](https://spring.io/blog/2016/07/28/reactive-programming-with-spring-5-0-m1) to make my point,
so be very careful when choosing Spring as the high-available, highly performant __core__ of your reactive system:

> You the developer can choose what’s better for your purposes (non-blocking vs blocking). If anyone tells you that synchronous or blocking is evil look the other way. It’s not and in reality it is a trade-off. Imperative style logic is simple to write and simpler to debug. Sure it doesn’t scale as well or as efficiently but that’s where the trade-off comes. There will always be many cases where imperative is just fine for the task at hand and others where reactive and non-blocking are a must. In a microservices scenario, you may even choose the implementation style per individual service, all within the same consistent programming model.

## Modes of concurrency
We now know that in reactive systems the execution model is very different than in traditional web frameworks like JEE
and Spring. We can summize that there are four modes of concurrency in systems, from best (1) to worst (4):

1. Asynchronous and non-blocking (excellent)
2. Asynchronous and blocking (acceptable)
3. Synchronous and non-blocking (meh)
4. Synchronous and blocking (awful!)

Traditional web frameworks use the synchronous and blocking concurrency model which is mode 4 and is the traditional
JEE API. Reactive frameworks strive to provide a mode (1) asynchronous and non-blocking execution model and the Lagomframework
and Playframework provides such an execution model. Most of the time the execution model still doesn't get better than
mode 2 which is asynchronous and blocking for some pieces of the system and that is party because of the serialization
services that a systems needs like for example reading or writing to a file, communicating with a database. Most IO operations
do have some blocking quality in it, enforced by eg. the Linux kernel, security constraints, JDBC request/response cycles,
memory sychronization between context switch, sufficive to say a technical reason.

Hybrid frameworks like Play and Lagom can isolate these blocking parts, which needs upfront design, and the concurrency
model is (2) for some parts of the system and (1) for most parts of the system. When used with the most modern subsystems
like Apache Cassandra and Apache Kafka like with Lagom the concurrency model is (1) which is excellent!

## Where does Spring fit really?
Sometimes people ask me, especially when they already have invested in Java and Spring why investing in a new paradigm,
execution model and tooling is a necessity when the Spring vendor communicates that the traditional execution model
has been brought up to spec with some small alterations and is good enough for building a reactive system which is
based on a message driven architecture, event streaming processing pipelines and structured using the microservice
architecture style with API first design using many of the APIs developers already know and just providing some
asynchronous callbacks and you'll get a concurrency (2) mode so Asynchronous and blocking mode.

To answer this, lets look at the frameworks first

### Spring-Boot
Spring-boot Takes an opinionated view of building Spring applications. Spring Boot favors convention over configuration
and is designed to get you up and running as quickly as possible. Spring reuses a lot of the legacy APIs and provides,
a mode (2) concurrency model but doesn't enforce it for only the spring-mvc part so for providing asynchronous REST services.

Spring-boot embeds Tomcat, Jetty or Undertow which are all servlet containers and all except undertow uses the thread-per-request
model but if you are not careful you will get a mode (4) concurrency model (synchronous and blocking) which is not good for
building a reactive system.

Spring provides a lot out of the box like a runtime platform (server), cloud services like service-discovery, runtime configuration,
circuit breakers, cloud database connectors, integration and batch standards, too much to name.

That there is so much to choose from can sound great, must it really isn't. A good platform should be 'full-stack' which means
it should provide highly optimized APIs and a documented pattern for using these APIs together in a certain way.
If you are working with a 'full-stack' platform, you know that you’ll be able to make the separate components work together
optimally and you are certain that you are working with a good designed application architecture.

Spring gives many options and a lot of those options don't work well together in context of a reactive system. It isn't good
enough to just have a batch processing framework, like with spring-batch, you should have an elastic scalable reactive batch
processing framework like Hadoop or even better Apache Spark.

Spring doesn't enforce the goals of reactive systems and focusses too much on integrating cool new features and enabling them
using annotations which seem like a good idea when creating singleton services, but when it comes on the execution model and
you end up with mode (4) components that must be composed together the system will just not perform correctly which is a shame.

I guess the gist is that you just cannot compose a system based on a down-scaled legacy-based n-tier architecture from
yesterday. You need a new execution model, adopt a new computational paradigm based on pure computations in where
the evaluation can be expressed rather than imperatively written down making the system inflexible in the evaluation order
and optimization strategies, isolation of functionality;failure;read/write;bounded context;immutable record of facts vs query model,
messaging and distribution, dual communication streams (demand/message), basically you need a new paradigm and architecture
and the two are incomparable.

### Playframework
The Playframework (Play) is a modern, efficient, cloud-ready web-server with a strong focus on developer productivity. Play is
the basic building block of a modern reactive system that is structured using the microservice architecture with API first in mind.

Play is build on Netty a NIO based client-server asynchronous event-driven network application framework for
developing high performance protocol servers and on Akka a distributed computing toolkit that brings resilience
and elasticity to the JVM. Although Play supports a full features Java based API the development experience
really shines when Play is used with Scala, a hybrid object-functional general purpose programming language,
bringing the best aspects of both approaches to help developers craft elegant software.

### Lagomframework
Lagom is an opinionated microservices framework that builds on Play and provides a unique developer friendly way
to easily create reactive microservice based systems that is based on CQRS and event sourcing and Apache Kafka,
a distributed streaming platform used to build real time data-pipelines between your services.

## Server engines
Lets take a look at the server engines that are used by the three frameworks.

### Tomcat (Threading Server)
[Apache Tomcat](http://tomcat.apache.org/) is an open source implementation of the Java Servlet, JavaServer Pages,
Java Expression Language and Java WebSocket technologies. Apache Tomcat can be used by spring-boot.

### Jetty (Threading Server)
[Jetty](http://www.eclipse.org/jetty/) is a Java HTTP server and Java Servlet container and as of
v9.1 supports Servlet 3.1 which supports asynchronous/non-blocking IO. Jetty can be used by spring-boot.

### JBoss Undertow (Eventing Server)
[JBoss Undertow](http://undertow.io/) is a flexible performant web server written in java, providing both blocking and
non-blocking API’s based on NIO. JBoss Undertow is the default web server in the [Wildfly Application Server](http://wildfly.org/).
JBoss Undertow can be used by spring-boot

### Netty (Eventing Server)
[Netty](http://netty.io/) is a NIO based client-server asynchronous event-driven network application framework for
developing high performance protocol servers and clients. Netty is used by the Lagomframework or the Playframework.

### Conclusion
When looking at the servers one thing sticks out, Tomcat, Jetty and Undertow are all JEE compliant servers which basically means
being a stack of interfaces so you can do both blocking and non-blocking IO and the non-blocking part of JEE seems like an
afterthought. When looking at the Java stack its obvious that the frameworks are biased for mutability, transactional, thread-per-request,
blocking IO and when you want to defer from this bias things gets ackward.

## JSR-315/Servlet 3.0
In December 2009 the Servlet 3.0 specification was released as a part of Java EE 6. This was an important release
in terms of standardization of how to perform non-blocking processing towards the underlying web servers and frameworks.
With Servlet 3.0 a non-blocking application can be deployed on any web server that supports the specification,
e.g. GlassFish, Tomcat, Jetty, Resin or any of the commercial alternatives.

## JSR-340/Servlet 3.1
In December 2013 Spring 4.0 was released with an unparalleled simplicity for developing non-blocking REST services
using Spring MVC and deploying them on any Servlet 3.0 compliant web server using Spring Boot.

One of the key features added in the [Servlet 3.1 JSR 340](http://jcp.org/en/jsr/detail?id=340) is asynchronous
(aka non-blocking) IO. Servlet 3.0 introduced asynchronous servlets, which could suspend request handling to
asynchronously handle server-side events. Servlet 3.1 now adds IO with the request/response content as events
that can be handled by an asynchronous servlet or filter. The Servlet 3.1 API is available in the Jetty-9.1 branch

New methods to activate Servlet 3.1 asynchronous IO have been added to the ServletInputStream and ServletOutputStream
interfaces that allow listeners to be added to the streams that receive asynchronous callbacks.
The listener interfaces are WriteListener and ReadListener.

## Blocking REST
Blocking basically is when the request thread is locked during the processing of a request like with the following method:

```scala
@RestController
class ProcessingController {
  @RequestMapping("/process-blocking")
  def blockingProcessing(): ProcessingStatus = {
    new ProcessingStatus()
  }
}
```

The same blocking call can be made in Play of course:

```scala
def processBlocking = Action(Ok(Json.toJson(new ProcessingStatus()))
```

The problem, from a scalability perspective, is that the request thread is locked during the processing of this method.
If the method needs to make a long running call to an external resource, such as another REST or SOAP service or a database,
the request thread will be blocked during the wait for the external resource to respond.

## Non-blocking REST
To avoid the blocking of the request thread the programming model is changed to a callback model. The REST service
doesn’t return the actual result to the Servlet container but instead an object, called a `DeferredResult`,
that will receive the result at some time in the future. The result will be filled in by some other thread,
typically using a callback-object. Spring MVC will hand over the result to the Servlet container
that sends it back to the client. In the REST service we have to initiate this processing before we return the
DeferredResult object to the Servlet container like:

```scala
@RestController
class ProcessingController {

  @RequestMapping("/process-non-blocking")
  def nonBlockingProcessing(): DeferredResult[ProcessingStatus] = {

    // Initiate the processing in another thread
    val deferredResult: DeferredResult[ProcessingStatus] = new DeferredResult()
    val task: ProcessingTask = new ProcessingTask(deferredResult, ...)
    dispatch(task)

    // Return to let go of the precious thread we are holding on to...
    deferredResult
  }
}
```

We also need to create a callback handler that will eventually be 'called-back' which is the 'ProcessingTask'.

## Non-blocking
Non-blocking I/O has been supported by the Java platform since 2002 with Java SE v1.4 and its API’s called New I/O (NIO).
It was initially hard to use, specifically with portability in mind. A number of Java based web servers and frameworks for HTTP,
such as Jetty and Netty, evolved to fill the gaps and today they provide a solid ground for non-blocking I/O, but with product
specific API’s.

## Blocking and Non-blocking
The key objective of being asynchronous is to avoid blocking.  Every blocked thread represents wasted resources as the
memory allocated to each thread is significant and is essentially idle whenever it blocks.

Blocking also makes your server vulnerable to thread starvation. Consider a server with 200 threads in it’s thread pool.
If 200 requests for large content are received from slow clients, then the entire server thread pool may be consumed by
threads blocking to write content to those slow clients. Asynchronous IO allows the threads to be reused to handle other
requests while the slow clients are handled with minimal resources.

Jetty has long used such asynchronous IO when serving static content and now Servlet 3.1 makes this feature available
to standards based applications as well.

Non Blocking IO has the following advantages:

- Highly Scalable: Because no-more you require one thread per client. It can effectively support more number of clients.
- High Keep Alive: Blocking IO requires to block until the keepalive time for the next request. Non-Blocking being notification model, it can support high keepalive times.
- Better Performance on High Load: Because in blocking IO has one thread per connection, it requires n threads for n connections. As the value n increases, the performance degrades because more thread context switching.

## Resources
- [The Reactive Manifesto](http://www.reactivemanifesto.org/)
- [Reactive Streams](http://www.reactive-streams.org/)
- [Threaded vs Evented Servers - Mark McGranaghan](https://mmcgrana.github.io/2010/07/threaded-vs-evented-servers.html)
- [Asynchronous processing support in Servlet 3.0 Why asynchronous processing is the new foundation of Web 2.0](http://www.javaworld.com/article/2077995/java-concurrency/java-concurrency-asynchronous-processing-support-in-servlet-3-0.html)
- [Servlet 3.1 Asynchronous IO and Jetty-9.1](https://webtide.com/servlet-3-1-async-io-and-jetty/)
- [C10k: Developing non-blocking REST services with Spring MVC](http://callistaenterprise.se/blogg/teknik/2014/04/22/c10k-developing-non-blocking-rest-services-with-spring-mvc/)
- [In Search for a Scalable & Reactive Architecture of a Cloud Application: CQRS and Event Sourcing Case Study](http://www.icsr.agh.edu.pl/~malawski/DebskiSzczepanik-CQRS-IEEE-Software.pdf)
- [Isolates, Channels, and Event Streams for Composable Distributed Programming](http://axel22.github.io/resources/docs/reactive-isolates.pdf)
- [Thread per request - LYCOG](http://lycog.com/distributed-systems/thread-per-request/)
- [Scalable, Robust - and Standard - Java Web Services with Fibers](http://blog.paralleluniverse.co/2014/05/29/cascading-failures/)
- [Little's Law, Scalability and Fault Tolerance: The OS is your bottleneck (and what you can do about it)](http://blog.paralleluniverse.co/2014/02/04/littles-law/)
- [Exploring the virtues of microservices with Play and Akka](https://zeroturnaround.com/rebellabs/exploring-the-virtues-of-microservices-with-play-and-akka/)
- [Play framework and async I/O - Yevgeniy Brikman](https://engineering.linkedin.com/34/play-framework-and-async-io)
- [Play Framework: async I/O without the thread pool and callback hell - Yevgeniy Brikman](https://engineering.linkedin.com/play/play-framework-async-io-without-thread-pool-and-callback-hell)
- [Work Stealing: What Makes the Play Framework Fast - Kevin Webber](https://blog.redelastic.com/work-stealing-what-makes-the-play-framework-fast-4b71fa7758d5#.bpj17ghu9)
- [Futures and Promises](http://docs.scala-lang.org/overviews/core/futures.html)
- [Choosing an ExecutorService](http://blog.jessitron.com/2014/01/choosing-executorservice.html)
- [Reactive Programming with Spring 5.0 M1](https://spring.io/blog/2016/07/28/reactive-programming-with-spring-5-0-m1)
- [Reactor - a foundation for asynchronous applications on the JVM](https://spring.io/blog/2013/05/13/reactor-a-foundation-for-asynchronous-applications-on-the-jvm)

## Youtube
- [The Play Framework at LinkedIn: Productivity and Performance at Scale - Jim Brikman](https://www.youtube.com/watch?v=8z3h4Uv9YbE)
- ["Node.js v.s. Play Framework" - Jim Brikman](https://www.youtube.com/watch?v=b6yLwvNSDck)
- [State of Netty - Trustin Lee](https://www.youtube.com/watch?v=0aoeSsKarc8)
- [Netty Best Practices - Norman Maurer](https://www.youtube.com/watch?v=WsMOJqAYW5M)
- [From Spring + Java to Spring + Akka - A Journey of Discovery - Nilanjan Raychaudhuri and Josh Suereth](https://www.youtube.com/watch?v=fALUf9BmqYE)
- [Cloud Native Java - Josh Long](https://www.youtube.com/watch?v=JDcl4kT6Qmo)
- [Optimizing Play for Production - Lightbend](https://www.youtube.com/watch?v=cnPPLpIk9mo)
- [Revitalizing Aging Architectures with Microservices - Lightbend](https://www.youtube.com/watch?v=SPGCdziXlHU)
- [Reactive Revealed 1/3: Async NIO, Back-pressure and Message-driven vs Event-driven - Konrad Malawski](https://www.youtube.com/watch?v=fNEZtx1VVAk)