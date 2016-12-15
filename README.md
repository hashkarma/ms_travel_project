Asynchronous Travel microservice

This microservice project gets quotes for airlines or airlines + hotels or airlines + hotels + cars asynchronously using JDK 1.8 feature known as CompletableFuture.

It covers the following scenarios :
1) An independent task which can spawn into multiple tasks.
2) 2 independent tasks called and then their result combined using a method.
3) 2 independent tasks called, their result combined and then a third task called which is dependant on the result of the previous 2 tasks.
4) Call n number of tasks asynchronously and then wait for their completion using allOf and then combine their results.
