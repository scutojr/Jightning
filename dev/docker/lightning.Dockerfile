ARG BTC_VERSION

FROM bitcoin:$BTC_VERSION

ARG LN_VERSION 

RUN pip3 install mako && \
    yum install gmp-devel libsqlite3x-devel.x86_64 gettext -y && \
    git clone https://github.com/ElementsProject/lightning.git && \
    cd lightning && \
    git checkout tags/$LN_VERSION -b $LN_VERSION && \
    ./configure && \
    make install
