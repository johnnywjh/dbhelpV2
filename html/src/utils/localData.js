// 本地持久存储
const LocalData = {
    data(table, settings, storage) {
        table = table || 'dbhelp';
        storage = storage || localStorage;

        // if(!win.JSON || !win.JSON.parse) return;

        // 如果 settings 为 null，则删除表
        if (settings === null) {
            return delete storage[table];
        }

        settings = typeof settings === 'object'
            ? settings
            : {key: settings};

        try {
            var data = JSON.parse(storage[table]);
        } catch (e) {
            var data = {};
        }

        if ('value' in settings) data[settings.key] = settings.value;
        if (settings.remove) delete data[settings.key];
        storage[table] = JSON.stringify(data);

        return settings.key ? data[settings.key] : data;
    },
    sessionData(table, settings) {
        return this.data(table, settings, sessionStorage);
    }
}
export default LocalData