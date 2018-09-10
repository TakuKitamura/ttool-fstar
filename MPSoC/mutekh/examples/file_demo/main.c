
#include <vfs/vfs.h>
#include <libvfs/fs/ramfs/ramfs.h>

#include <mutek/printk.h>
#include <stdio.h>

static
struct vfs_node_s * vfs_init()
{
	struct vfs_fs_s *root_fs;

    printk("init vfs... ");
	ramfs_open(&root_fs);
    printk("ok\n");

    struct vfs_node_s *root_node;
    vfs_create_root(root_fs, &root_node);

	return root_node;
}

bool_t do_write()
{
    FILE *file = fopen("/toto.txt", "w");

    if ( file == NULL ) {
        printf("File open failed for writing\n");
        return 1;
    }

    fprintf(file, "Test of fprintf: %d\n", 42);
    fclose(file);

    return 0;
}

bool_t do_read()
{
    FILE *file = fopen("/toto.txt", "r");

    if ( file == NULL ) {
        printf("File open failed for reading\n");
        return 1;
    }

    char buffer[128];
    size_t r = fread(buffer, 1, 128, file);
    printk("fread returned %u\n", r);
    writek(buffer, r);
    fclose(file);
    return 0;
}

void app_start()
{
    vfs_init();

    bool_t err;

    err = do_write();
    if ( err )
        return;

    err = do_read();
    if ( err )
        return;
}
