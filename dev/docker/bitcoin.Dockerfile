FROM centos:7

ARG BTC_VERSION

ADD resource/epel-release-7-12.noarch.rpm /tmp/

RUN rpm -Uvh /tmp/epel-release-7-12.noarch.rpm && \
    yum update -y

RUN yum install git gcc-c++ libtool make autoconf automake openssl-devel libevent-devel boost-devel libdb4-devel libdb4-cxx-devel python3 -y && \
    yum install miniupnpc-devel -y && \
    yum install zeromq-devel -y && \
    yum install qrencode-devel -y && \
    yum install protobuf-devel -y && \
    yum install vim tmux -y

WORKDIR /root/

RUN git clone https://github.com/bitcoin/bitcoin.git && \
    cd bitcoin && \
    git checkout tags/$BTC_VERSION -b $BTC_VERSION && \
    ./autogen.sh && \
    ./configure --without-gui && \
    make install
