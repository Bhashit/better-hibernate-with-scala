# Hibernate, Guice and Play with Scala

**Updated to play 2.4.2 with Guice and Hibernate 5.**

## About this project


This is an POC project to show that hibernate can integrate well with
Scala and Play, without using nulls, java-bean accessors or too much
boilerplate. 

This project was extracted from another, under-development
project. I did that hoping that it would be helpful to someone else
like me; someone who doesn't think that ORMs are dead when you do
functional programming. 

I have done a lot of research on getting the persistence layer right for my
scala based projects. And I have come to the conclusion that ORMs are
here to stay for a while. I am constantly on the lookout for a good
way to keep using an ORM (for CRUD or object graphs) with another
framework like Slick or JOOQ (for complex queries).

Meanwhile, I have found a good way to make Hibernate work with scala's
Option types and the scala accessor methods.

There is a [companion blog post][blog-post] that I wrote which
explains, in more detail, what I am trying to do with this project.

**Disclaimer**: Maintenance of this project is not a high priority for
  me right now. Also, I am aware that a lot of code in this project
  begs to be improved. I know :expressionless:. But this was a
  personal POC kind of thing. My own project, for which I was doing
  this experiment, has a much cleaner structure. 


## Building and Running

### Database

Set the username and password for a database user in
`conf/hibernate.properties`.  Make sure that this user can create
tables since `hbm2ddl.auto` is set to `update`.

### Environment

The application requires the path of [aspectjweaver.jar][ajweaver] as
a value of `ACTIVATOR_OPTS` env variable before getting launched. This
allows the weaver jar to be used as a `javaagent` to allow
[load-time weaving](ltw). 

You can download [aspectj-weaver.jar][ajweaver] somewhere and specify
its path in the `launch` file (in the root dir of the project) for
the variable `ACTIVATOR_OPTS`.

The line looks like this:

```
export ACTIVATOR_OPTS="-javaagent:${userhome}/.ivy2/cache/org.aspectj/aspectjweaver/jars/aspectjweaver-1.8.2.jar"
```

Replace the path after "-javaagent:" with your download path.

### Building the aspects

> You won't need to do this step if you are not going to make any
> changes to the aspects related code located inside the
> "hibernate-property-access" dir.

To make load-time weaving work with _third party_ classes in play, the
aspects and the related classes need to be specified as a
dependency. I'll write more about it later. This is true only for the
development mode in play.

For that, I have created a small inner project inside the
"hibernate-property-access" dir in the project's root dir. I can't use
it as an sbt sub-project since that would make play use its
application class-loader for its classes and that would defeat the
whole purpose.

For the time being, I am doing the quickest possible thing. run `sbt
package` inside "hibernate-property-access" dir and copy the generated
jar into the "lib" dir of the root project. There are several ways this
can be done better, but again, this is a POC. So, I am going to
use the quickest way possible. 

### Run

Use the `launch` file in the root dir of this project to launch
activator. It behaves in exactly same same way the `activator`
command.

I am aware that I haven't created a `launch.bat` for windows
users yet.

## More Details

You can read this [painfully long blog post][blog-post] if you want to
understand the exact details of what I am doing in this project.

[blog-post]: http://bhashitparikh.com/2014/10/28/hibernate-with-scala-options-with-less-pain.html
[play-framework]: http://playframework.com/
[ajweaver]: http://mvnrepository.com/artifact/org.aspectj/aspectjweaver/1.8.3
[ltw]: https://www.eclipse.org/aspectj/doc/next/devguide/ltw.html


