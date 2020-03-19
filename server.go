package main

import (
	"log"
	"net/http"
	"os"

	"github.com/99designs/gqlgen/graphql/handler"
	"github.com/99designs/gqlgen/graphql/playground"
	"github.com/l4nn1312/redblackrose/graph"
	"github.com/l4nn1312/redblackrose/graph/generated"

	"github.com/gin-gonic/gin"
)

const defaultPort = "8080"


func main() {
	// Setting up Gin
	r := gin.Default()
	r.Use(gzip.Gzip(gzip.DefaultCompression))
	r.LoadHTMLGlob("templates/*")
	r.Static("/bundles", "./bundles")

	srv := handler.NewDefaultServer(generated.NewExecutableSchema(generated.Config{Resolvers: &graph.Resolver{}}))
	playg := playground.Handler("GraphQL playground", "/query")
	// routes
	r.POST("/query", gin.WrapH(srv))
	r.GET("/", gin.WrapH(playg))

	r.Run()
}
