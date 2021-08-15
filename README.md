# tapir-play-sample

Sample project using [Play Framework](https://github.com/playframework) + [Tapir](https://github.com/softwaremill/tapir) (including OpenAPI documentation).

**Contributions are welcomed!**

If you find this project useful but think it could contain more useful examples (and it sure could ;)), please open an issue or a pull request.
I will also try to add new examples when I find one.

_Note: consider this project as a showcase._
_You probably want to structure things differently for a real-life project._

## Changelog

See [here](./CHANGELOG.md).

## Try it!

```
sbt run
```

And go to http://localhost:9000/docs/.

## Content

- Basic endpoints with shared logic, see `BookEndpoints`
  - Reading input body
  - Reading input authentication headers and shared authentication logic
  - Sending output body and errors
  - Streaming request/response
- Swagger Documentation, see `ApiDocumentation` 

## Takeaway

### Router

_Forget the `routes` file!_

To use Tapir with Play, you have to use a SIRD Router:
basically a router defined by the code rather than by the `routes` file.

The `routes` file looks like this:
```
->        /        routers.ApiRouter
```

**Note:** you can still use regular routes defined in the `routes` file.
Both can be used within the same app.

### Controllers

_Forget Play's `Action { ... }`!_

The "controller" consists of regular methods returning `Future[Either[L,R]]`.

### Open API Documentation

Tapir generates the OpenAPI YAML from your endpoints definitions.

You can then either:
- expose the YAML (through Tapir or not) and use it from a Swagger UI deployed by yourself the way you want (more customizable)
- let Tapir exposes everything automatically with the `tapir-swagger-ui-play` dependency (as in this sample)

## Documentation

- Tapir: https://tapir.softwaremill.com/en/latest/quickstart.html
- Play SIRD Router: https://www.playframework.com/documentation/2.8.x/ScalaSirdRouter#Binding-sird-Router
