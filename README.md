# tapir-play-sample

Sample project using [Play Framework](https://github.com/playframework) + [Tapir](https://github.com/softwaremill/tapir) (including OpenAPI documentation).

**Contributions are welcomed!** If you find this project useful but think it could contain more useful examples (and it sure could ;)), please open an issue or a pull request.

_Note: consider this project as a showcase._
_You can structure things differently for a real-life project._

## 📖 Guided tour

For a guided tour of how to use Tapir with Play Framework,
you can read the following article: https://medium.com/@gaeljw/using-tapir-in-a-play-framework-application-d75a93d85030

## 🚀 Try it!

```
sbt run
```

And go to http://localhost:9000/docs/.

## ✅ Content 

- Basic endpoints with shared logic, see `BookEndpoints`
  - Reading input body
  - Reading input authentication headers and shared authentication logic
  - Sending output body and errors
  - Streaming request/response
    - With multiple formats for output and supporting complex `Accept` headers
- Swagger Documentation, see `ApiDocumentation` 

## 📜 Changelog

See [here](./CHANGELOG.md).
