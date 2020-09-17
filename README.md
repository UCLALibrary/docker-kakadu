## A Docker Image for the Kakadu JPEG-2000 Library
[![Build Status](https://travis-ci.com/UCLALibrary/docker-kakadu.svg?branch=main)](https://travis-ci.com/UCLALibrary/docker-kakadu) [![Known Vulnerabilities](https://snyk.io/test/github/uclalibrary/docker-kakadu/badge.svg)](https://snyk.io/test/github/uclalibrary/docker-kakadu)

This project builds a Docker image from the Kakadu JPEG-2000 library. Its purpose is to serve as a base for other Docker images. Because Kakadu is proprietary software, we cannot make the base image that we've built available. Anyone should, however, be able to build their own base image with this project (and a properly licensed copy of Kakadu's source code).

In order to do this, there needs to be an additional GitHub repo in which the Kakadu source code is kept. At the root of this repository should be the directory in which Kakadu Software distributed their code. This directory will have a name something like `v7_A_7-01805E`. You also need to have Java, [Docker](https://docs.docker.com/get-docker/), and [Maven](https://maven.apache.org/) installed on your local machine in order to build this project.

### Building the Project

To build a Kakadu image, two additional parameters need to be supplied to the Maven build. These are the version of Kakadu that you have (which is the same as the directory name that Kakadu Software has given you) and the location of your Kakadu GitHub repo where this directory lives:

    mvn -Dkakadu.version=v7_A_7-01805E \
     -Dkakadu.git.repo=scm:git:git@github.com:uclalibrary/kakadu.git verify

_We use `verify` instead of `package` because there are tests in the verify stage that will be run against the newly build container to make sure it's built and configured like it should be._

### UCLA Specific Instructions

Folks from UCLA should ask someone from the Services team for the `kakadu.version` value that they should use. The `kakadu.git.repo` variable can be omitted because the project is set up to use UCLA's Kakadu repository by default. In order to access our private repository, an account on GitHub is required. We'll also have to give that account access to the private Kakadu repository.
 
If you'd like to use our pre-built Docker image (which won't require that you have Java or Maven installed on your local machine), an account on [DockerHub](https://hub.docker.com/) is required. Contact us and we'll be glad to help you get set up with access.

### Running the Container

Running the build above will produce a Docker image. This image can be used as a base image for other Docker containers or it can be used to run Kakadu on a TIFF image locally. To do this, copy a TIFF image (e.g., test.tif) into your local directory and run:

     docker run --rm -v $(pwd):/home/kakadu kakadu test.tif

This will produce output that looks like:

    Note:
        The default rate control policy for colour images employs visual (CSF)
        weighting factors.  To minimize MSE instead, specify `-no_weights'.
    Note:
        If you want quality scalability, you should generate multiple layers with
        `-rate' or by using the "Clayers" option.
    
    Generated 1 tile-part(s) for a total of 1 tile(s).
    Code-stream bytes (excluding any file format) = 372,543 = 0.745086 bits/pel.
    Compressed bytes (excludes codestream headers) = 372,308 = 0.744616 bpp.
    Body bytes (excludes packet and codestream headers) = 365,034 = 0.730068 bpp.
    Layer bit-rates (possibly inexact if tiles are divided across tile-parts):
            0.745086
    Layer thresholds:
            0
    Processed using the multi-threaded environment, with
        8 parallel threads of execution
    
If the image cannot be processed by Kakadu, you will see an error message. You can also use the container to get more information about a JP2 or JPX file. To do this, copy the file (e.g., test.jp2) to your local directory and run:

    docker run --rm -v $(pwd):/home/kakadu kakadu test.jp2

This will produce output that looks like:

    <JP2_family_file>
      <ftyp name="file-type box" header="8" body="12" pos="12">
        <brand> "jp2_" 0x6A703220 </brand>
        <minor_version> 0 </minor_version>
        <compatible_brand> "jp2_" 0x6A703220 </compatible_brand>
      </ftyp>
      <jp2h name="JP2-header box" header="8" body="63" pos="32">
        <ihdr name="image-header box" header="8" body="14" pos="40"></ihdr>
        <colr name="colour box" header="8" body="7" pos="62"></colr>
        <res_ name="resolution box" header="8" body="18" pos="77">
          <resc name="capture-resolution box" header="8" body="10" pos="85"></resc>
        </res_>
      </jp2h>
      <jp2c name="contiguous-codestream box" header="8" body="rubber" pos="103">
        <codestream>
          <width> 2000 </width>
          <height> 2000 </height>
          <components> 4 </components>
          <tiles> 1 </tiles>
        </codestream>
      </jp2c>
    </JP2_family_file>

If the JP2 cannot be read by Kakadu, you will see an error message. These examples assume you're running either Linux or Mac OS. If you're using Windows and don't have a Bash shell available to you, you will need to change the syntax of the command line examples to work on Windows' shell.

### Publishing the Image

If you're not from UCLA and would like to publish your Kakadu base image to DockerHub, you can do that by running the Maven `deploy` plugin with the following arguments:

    mvn -Ddocker.registry.username=[USERNAME] -Ddocker.registry.password=[PASSWORD] \
     -Ddocker.registry.account=[ACCOUNT] -Dkakadu.version=[KAKADU_VERSION] \
     -Dkakadu.git.repo=[GITHUB_REPO] deploy

This will publish your newly built Docker image into your DockerHub account. Make sure the image is marked as private in DockerHub unless you have explicit permission from Kakadu Software to redistribute binaries built from their code.

### Additional Things to Know

#### Submodule Warnings

Once you've built the project, you might get the following warning:

    warning: adding embedded git repository: src/main/docker/kakadu
    hint: You've added another git repository inside your current repository.
    hint: Clones of the outer repository will not contain the contents of
    hint: the embedded repository and will not know how to obtain it.
    hint: If you meant to add a submodule, use:
    hint: 
    hint:   git submodule add <url> src/main/docker/kakadu
    hint: 
    hint: If you added this path by mistake, you can remove it from the
    hint: index with:
    hint: 
    hint:   git rm --cached src/main/docker/kakadu
    hint: 
    hint: See "git help submodule" for more information.

This is what you want. You do not want to add your Kakadu code as a submodule since the repository is private and should not be linked to this project's code.

#### Using the UCLA Image

If you're from UCLA and using a pre-built Docker image, you will need to include our DockerHub account on the above commands; for instance:

    docker run --rm -v $(pwd):/home/kakadu uclalibrary/kakadu test.tif

You will need to have access to that private repository, too, so please contact someone on the Services team to help you get set up on DockerHub.

#### Using Image Files in Other Directories

It is also possible to test an image that is not in your current directory. This can be done in one of two ways: by extending the image file path or by changing the volume mount point:

    docker run --rm -v $(pwd):/home/kakadu kakadu src/test/resources/images/test.jp2

or

    docker run --rm -v $(pwd)/src/test/resources/images/:/home/kakadu kakadu test.jp2

The important thing to remember is that the first value passed to the `-v` argument must be an absolute path. You don't have to use the `$(pwd)` shorthand... a full path can also be passed:

    docker run --rm \
     -v /home/kevin/docker-kakadu/src/test/resources/images/:/home/kakadu \
     kakadu test.jp2

#### Other Things

That's it. If you encounter any other possible stumbling blocks, please let us know.

### Contact Us

We use an internal ticketing system, but we've left the GitHub [issues queue](https://github.com/UCLALibrary/docker-kakadu/issues) open in case you'd like to file a ticket or make a suggestion.
